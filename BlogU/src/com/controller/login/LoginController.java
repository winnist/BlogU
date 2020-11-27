package com.controller.login;

import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import com.model.member.MemberService;
import com.model.member.MemberVO;
import com.model.post.PostVO;

@Controller
@RequestMapping("/*")
public class LoginController {
	
	@Autowired
	MemberService memberSvc;
	
	
	
	@RequestMapping(method=RequestMethod.GET, value="/*")
	public String loadIndexPage2(ModelMap model) {
		model.addAttribute("memberVO", new MemberVO());
		System.out.println("login2--------------"+model.containsKey("memberVO2"));
		return "index";
	}
	
	@RequestMapping(method=RequestMethod.GET, value="/index")
	public String loadIndexPage(ModelMap model) {
		model.addAttribute("memberVO", new MemberVO());
		System.out.println("login--------------"+model.containsKey("memberVO2"));
		return "index";
	}
	
	@RequestMapping(method=RequestMethod.POST, value="login")
	public String login(@Email @NotEmpty @RequestParam("email")String email, 
			@NotEmpty @RequestParam("password")String password, ModelMap model,HttpServletRequest req) {
		
		MemberVO memberVO = memberSvc.findByLoginInfo(email, password);
		System.out.println("login" + memberVO);
		if(memberVO == null) {
			model.addAttribute("loginMsg", "帳號或密碼錯誤");
			return "index";
		}
		
		req.getSession().setAttribute("memberId", memberVO.getMemberId());
		
		Set<PostVO> posts = memberSvc.getPostsByMemberId(memberVO.getMemberId());
		model.addAttribute("postsByMemberId", posts);
		return "post/listAllPost";
	}
	
	@ExceptionHandler(value = { ConstraintViolationException.class })
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	public ModelAndView handleValidError(HttpServletRequest req,ConstraintViolationException e) {
	    Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
	    StringBuilder strBuilder = new StringBuilder();
	    for (ConstraintViolation<?> violation : violations ) {
	          strBuilder.append(violation.getMessage() + "<br>");
	    }
	    String message = strBuilder.toString();
	    return new ModelAndView("index", "message", "錯誤訊息:<br>"+message);
	}
}
