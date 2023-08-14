package poly.store;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import poly.store.entity.Account;
import poly.store.service.AccountService;

@Configuration
@EnableWebSecurity
public class SecurityConfig  extends WebSecurityConfigurerAdapter{
	@Autowired
	BCryptPasswordEncoder pe;
	
	@Autowired
	AccountService accountService;
	
	@Autowired
	HttpSession session;
	
	// Cung cấp nguồn dữ liệu đăng nhập
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(username -> {
			try {
				//2.Đầu tiên cta phải tải account để lấy username
				//3.xong từ accoutn lấy pass để ta mã hóa
				Account user = accountService.findById(username);
				String password = pe.encode(user.getPassword());
				//4.Lấy vai trò của ng dùng đổi sáng 1 mảng
				//5.để tạo ra userdetail
				String[] roles = user.getAuthorities().stream()
						.map(er -> er.getRole().getId())
						.collect(Collectors.toList())
						.toArray(new String[0]);
				
				Map<String, Object> authentication = new HashMap<>();
				authentication.put("user", user);
				byte[] token = (username + ":" + user.getPassword()).getBytes();
				authentication.put("token", "Basic " + Base64.getEncoder().encodeToString(token));
				session.setAttribute("authentication", authentication);
				
				//1.Hệ thống đưa cho ta username, cta phải trà về user detail thông qua username,pass và vai trò
				return User.withUsername(username).password(password).roles(roles).build();
			} catch (NoSuchElementException e) {
				throw new UsernameNotFoundException(username + " not found!");
			}
		});
	}
	
	// Phân quyền sử dụng
	@Override
	protected void configure(HttpSecurity http) throws Exception {	
		http.csrf().disable();
		http.authorizeRequests()
		//1.Mỗi cái url đưa ra 1 chính sách bảo mật
			.antMatchers("/order/**").authenticated() //Bắt buộc đăng nhập
			.antMatchers("/admin/**").hasAnyRole("STAF", "DIRE")
			.antMatchers("/rest/authorities").hasRole("DIRE")
			.anyRequest().permitAll();
		
		http.formLogin()
		//2.Mỗi form chỉ ra địa chỉ đăng nhập là gì-> sec control
			.loginPage("/security/login/form")	
			//3.Đây là địa chỉ mà form submit tới
			.loginProcessingUrl("/security/login")
			//4.Đến thành công đăng nhập
			//5.False là đăng nhập thành công ko nhất thiết trở lại form success
			.defaultSuccessUrl("/security/login/success", false)
			.failureUrl("/security/login/error");

		http.rememberMe()
			.tokenValiditySeconds(86400);
		
		http.exceptionHandling()
			.accessDeniedPage("/security/unauthoried");
		
		http.logout()
			.logoutUrl("/security/logoff")
			.logoutSuccessUrl("/security/logoff/success");		
	}
	
	// Cơ chế mã hóa mật khẩu
	@Bean
	public BCryptPasswordEncoder getPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	// Cho phép truy xuất REST API từ domain khác
	@Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers(HttpMethod.OPTIONS, "/**");
    }
}
