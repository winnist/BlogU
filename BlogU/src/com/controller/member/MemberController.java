package com.controller.member;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;

import org.json.simple.JSONArray;
import org.json.simple.JSONValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.model.member.MemberService;
import com.model.member.MemberVO;
import com.model.post.PostVO;

@Controller
@RequestMapping("/member")
@MultipartConfig
public class MemberController {

	@Autowired
	private MemberService memberSvc;
	
	@Autowired
	ServletContext servletContext;
	
	String photoSaveDirectory = "resources/img/photo_upload/";
	
	@RequestMapping(method=RequestMethod.GET, value="addMember")
	public String addMember(ModelMap model) {
		System.out.println("helloaddmember");
		//For addMember.jsp, Tag <form:form>'s modelAttribute to use. must have 
//		MemberVO memberVO = new MemberVO();
//		model.addAttribute("memberVO", memberVO);
//		
//		//List all member list
//		List memberList =  memberSvc.getAll();
//		model.addAttribute("memberList", memberList);
//		
		return "member/addMember";
	}
	
	/*
	 *  Method used to populate the List Data in view.
	 *  �p : <form:select path="deptno" id="deptno" items="${listData}" itemValue="deptno"  ="dname" />
	 *
	 *	Add memberVO and memberList to Model
	 */
	@ModelAttribute("memberList")
	protected List<MemberVO> referenceListData(@ModelAttribute("memberVO") MemberVO memberVO, ModelMap model) {
		System.out.println("hello2");
//		For addMember.jsp, Tag <form:form>'s modelAttribute to use. must have 
//		MemberVO memberVO = new MemberVO();
//		model.addAttribute("memberVO", memberVO);
		
		List<MemberVO> memberList = memberSvc.getAll();
		System.out.println("--"+memberList);
		System.out.println(JSONValue.toJSONString(memberList));
		System.out.println("jsonArray"+JSONArray.toJSONString(memberList));
		
		return memberList;
	}
	
	@RequestMapping(method = RequestMethod.GET, value="getAllMemberJsonStr")
	@ResponseBody
	public List<MemberVO> getAllMemberJson(ModelMap model, HttpServletRequest req){
		System.out.println("hell3");
		return memberSvc.getAll();
	}
	
	@RequestMapping(method = RequestMethod.GET, value="getAllMemberJsonStr2", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ResponseBody
	public String getAllMemberJson2(ModelMap model){
		System.out.println("hell4");
		ObjectMapper objMapper = new ObjectMapper();
		try {
			List<MemberVO> lists = memberSvc.getAll();
			String memberListJson = objMapper.writeValueAsString(lists);
			
			for(MemberVO vo: lists) {
				System.out.println(vo.getMemberId());
			}
			
			System.out.println(memberListJson);
			return memberListJson;
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
		return "JacksonWrong";
		
	}
	
	
	@RequestMapping(method = RequestMethod.POST, value = "insert")
	public String insert(@Valid MemberVO memberVO, BindingResult result, ModelMap model) {
		System.out.println("123"+memberVO.getMname());
		System.out.println(memberVO.getPassword());
		if(result.hasErrors()) {
			System.out.println("error insert");
			model.addAttribute("actionStatus","新增失敗");

			return "member/addMember";
		}
		
		memberSvc.addMember(memberVO);
		model.addAttribute("actionStatus","新增成功");
		
		return "member/listOneMember";		
	}
	
	@RequestMapping(method = RequestMethod.POST, value="delete")
	public String delete(@RequestParam("memberId") String memberId, ModelMap model) {		
		memberSvc.deleteMember(new Integer(memberId));
		model.addAttribute("actionStatus", "刪除會員成功");				
		model.addAttribute("memberList", memberSvc.getAll());
		return "member/addMember";
	}
	
	@RequestMapping(method=RequestMethod.POST, value="getOneForUpdate")
	public String getOneForUpdate(@RequestParam("memberId") String memberId, ModelMap model) {
		MemberVO memberVO = memberSvc.getOneMember(new Integer(memberId));
		System.out.println(memberVO.getEmail());
		
		model.addAttribute("memberVO", memberVO);
		return "member/updateMember";
	}
	
	@RequestMapping(method=RequestMethod.GET, value="listOneMember/{memberId}")
	public String listOneMember(@PathVariable("memberId")Integer memberId, ModelMap model) {
		MemberVO memberVO = memberSvc.getOneMember(memberId);
		model.addAttribute("memberVO", memberVO);
		return "member/listOneMember";
	}

	
	@RequestMapping(method=RequestMethod.POST, value="update")
	public String update(@Valid MemberVO memberVO, BindingResult result, @RequestPart("image") Part image, ModelMap model) {
		
		if(result.hasErrors()) {
			
			List<ObjectError> list = result.getAllErrors();
			String errormsg = null;
			for(ObjectError e : list) {
				errormsg = e.getCode()+e.getDefaultMessage();
				
			}
			model.addAttribute("actionStatus", "更新會員失敗"+errormsg+memberVO.getEmail()+memberVO.getMname());
			return "member/updateMember";
		}
		
			
		//Try 1: save photo to server
		System.out.println("photo"+image.getSubmittedFileName());
		if(image.getSubmittedFileName() != null && image.getSubmittedFileName().length()>0 && image.getContentType()!=null) {
			
			String realPath="";
			try {
				realPath = servletContext.getResource("/").getPath();
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String fsavePath = realPath + photoSaveDirectory;
			System.out.println("path"+fsavePath);
			File fsaveDirectory = new File(fsavePath);
			if(!fsaveDirectory.exists()) {
				fsaveDirectory.mkdirs();
			}
			
			String imgName = image.getSubmittedFileName();
			String imageType = imgName.substring(imgName.lastIndexOf("."));
			//give unique number to imgName to avoid image cache.
			String reName = memberVO.getMemberId()+String.valueOf(System.currentTimeMillis()) + imageType;
			File f = new File(fsaveDirectory, reName);
			
			try {
				image.write(f.toString());
				image.delete();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//delete old photo
			System.out.println(memberVO.getPhoto());
			if(memberVO.getPhoto()!= null && memberVO.getPhoto().length() > 0) {
				File oldPhoto = new File(fsavePath+memberVO.getPhoto());
				if(oldPhoto.exists()) {
					oldPhoto.delete();
					System.out.print("delete old photo success");
				}
			}
			memberVO.setPhoto(reName);
			
			
			
			/**
			 // Try 2: save photo to database;
			 
			InputStream in = photo.getInputStream();
			byte[] buf = new byte[in.available()];
			in.read(buf);
			in.close();
			memberVO.setPhoto(buf);
			**/
			
		}else {
			System.out.println("photo is null");
		}
		
		// photo could accept null
		
		memberSvc.updateProfile(memberVO.getMname(), memberVO.getPhoto(), memberVO.getBdate(), memberVO.getMemberId());
		model.addAttribute("actionStatus", "更新會員成功");
		return "member/listOneMember";
	}
	
	@RequestMapping(method=RequestMethod.POST, value="getPostsByMemberId")
	public String getAllPostsByMemberId(String memberId, ModelMap model) {
		 Set<PostVO> posts = memberSvc.getPostsByMemberId(new Integer(memberId));
		 model.addAttribute("postsByMemberId", posts);
		 return "post/listAllPost";
	}
	
	
	@ExceptionHandler(value = Exception.class)
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	public ModelAndView handleAllError(HttpServletRequest req,Exception e) {
		
	    String message = e.getMessage();
	    return new ModelAndView("errorPage", "message", "錯誤訊息:<br>" + message);
	}

}
