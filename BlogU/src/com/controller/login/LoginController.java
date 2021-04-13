package com.controller.login;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.model.member.MemberService;
import com.model.member.MemberVO;
import com.model.post.PostVO;

@Controller
@RequestMapping("/*")
public class LoginController {
	
	@Autowired
	MemberService memberSvc;
	private static String CLIENT_ID = "794664733369-9f9rmecr9uptjtm802r2nppp4tr3l6am.apps.googleusercontent.com";
	
	
	@RequestMapping(method=RequestMethod.GET, value="/*")
	public String loadIndexPage2(ModelMap model) {
		model.addAttribute("memberVO", new MemberVO());
		System.out.println("login2--------------"+model.containsKey("memberVO2"));
		return "index";
	}
	
	@RequestMapping(method=RequestMethod.GET, value="/index")
	@ResponseBody
	public String loadIndexPage(ModelMap model) {
		model.addAttribute("memberVO", new MemberVO());
		System.out.println("login--------------"+model.containsKey("memberVO2"));
		return "index";
	}
	
	@RequestMapping(method=RequestMethod.POST, value="signInGoogle")
	@ResponseBody
	public String signInGoogle(@RequestParam("idtoken")String idtokenString, ModelMap model, HttpServletRequest req) throws GeneralSecurityException, IOException {
		GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory()).
				setAudience(Collections.singletonList(CLIENT_ID)).build();
	
		GoogleIdToken idToken = verifier.verify(idtokenString);
		if(idToken != null) {
			Payload payload = idToken.getPayload();
			String userId = payload.getSubject();
			String email = payload.getEmail();
			String name = (String) payload.get("name");
			System.out.println(userId+email+name);
			boolean emailVerified = Boolean.valueOf(payload.getEmailVerified());
			System.out.println(userId+"---"+email+"---"+emailVerified);
			
			MemberVO memberVO = null;
			
			boolean beAuthorized = false;
			boolean beSignUp = false;
			
			memberVO = memberSvc.findByGoogleSub(userId);
			if(memberVO != null) beAuthorized = true;
			
			memberVO =  memberSvc.findByGoogleMail(email);
			if(memberVO !=null) beSignUp = true;
			
			if(!beAuthorized) {
				
			//case 1: Not Authorized, but Sign-up with email.
				if(beSignUp) {				
					memberVO.setGoogleSub(userId);
					memberSvc.updateMember(memberVO);
					
				}else {
					System.out.println(memberVO);
					//case 2: Not Authorized, not Sign-up.
					memberVO = new MemberVO();
					
					memberVO.setEmail(email);
					memberVO.setGoogleSub(userId);
					memberVO.setMname(name);
					
					memberSvc.addMember(memberVO);					
					System.out.println(memberVO);
				}
			}
			//case 3: Authorized.
			
			System.out.println(memberVO.getMemberId());
			req.getSession().setAttribute("memberId", memberVO.getMemberId());				
			Set<PostVO> posts = memberSvc.getPostsByMemberId(memberVO.getMemberId());
	
			model.addAttribute("postsByMemberId", posts);
			System.out.println("123");
			
			return "post/listAllPost/"+memberVO.getMemberId();
		}
		System.out.println("14423");
		model.addAttribute("loginMsg", "無此帳號");	
		
		
		return null;	
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
	/*
	 * @ExceptionHandler(value = { ConstraintViolationException.class })
	 * 
	 * @ResponseStatus(value = HttpStatus.BAD_REQUEST) public ModelAndView
	 * handleValidError(HttpServletRequest req,ConstraintViolationException e) {
	 * Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
	 * StringBuilder strBuilder = new StringBuilder(); 
	 * for (ConstraintViolation<?> violation : violations ) { strBuilder.append(violation.getMessage() +
	 * "<br>"); } String message = strBuilder.toString(); return new
	 * ModelAndView("index", "message", "錯誤訊息:<br>"+message); }
	 */
}
