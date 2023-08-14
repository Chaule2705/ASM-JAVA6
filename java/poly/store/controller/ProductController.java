package poly.store.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import poly.store.entity.Product;
import poly.store.service.ProductService;

@Controller
public class ProductController {
	/*ProductService dùng để thực hiện các phương thức mình mong muốn*/
	@Autowired
	ProductService productService;
	
	/*Ánh xạ url*/
	@RequestMapping("/product/list")
	/*Hiện tất cả sản phẳm*/
	/*cid là cái mã loại*/
	public String list(Model model, @RequestParam("cid") Optional<String> cid) {
		if(cid.orElse("").isEmpty()) {
			/*Công việc lấy sản phẩm nhờ pro-service*/
			List<Product> list = productService.findAll();
			/*Đưa list sp vào model để giao diện hiện ra*/
			model.addAttribute("items", list);
		}
		else {
			List<Product> list = productService.findByCategoryId(cid.get());
			model.addAttribute("items", list);
		}
		return "product/list";
	}
		
	@RequestMapping("/product/detail/{id}")
	/*Lấy mã sp thông qua id để hiện chi tiết sp*/
	public String detail(Model model, @PathVariable("id") Integer id) {
		Product item = productService.findById(id);
		model.addAttribute("item", item);
		return "product/detail";
	}
}