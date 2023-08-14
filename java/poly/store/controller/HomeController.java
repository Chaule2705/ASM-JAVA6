package poly.store.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {
	@RequestMapping({"/", "/home", "/home/index"})
	public String index() {
		return "redirect:/product/list";
	}
	//TỔ CHỨC ỨNG DỤNG THEO MÔ HÌNH SINGLE PAGE APPLICATION
	@RequestMapping("/admin/home/index")
	public String admin() {
		//TẠO 1 URL CHUYỂN SANG HTML THUẦN TÚY
		return "redirect:/assets/admin/index.html";
	}
}
