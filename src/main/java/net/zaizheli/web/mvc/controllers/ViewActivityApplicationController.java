package net.zaizheli.web.mvc.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import net.zaizheli.domains.Activity;
import net.zaizheli.domains.User;
import net.zaizheli.repositories.ActivityRepository;
import net.zaizheli.repositories.ApplicationRepository;
import net.zaizheli.web.utils.SessionUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/activity")
public class ViewActivityApplicationController {
	
	@Autowired
	ActivityRepository activityRepository;
	@Autowired 
	SessionUtil sessionUtil;
	@Autowired
	ApplicationRepository applicationRepository;
	
	@RequestMapping(value="/{id}/applications", method=RequestMethod.GET)
	public String view(@PathVariable String id, Model model, 
			HttpServletRequest request, HttpSession session){
		User user = sessionUtil.getSignInUser(session);
		if(user == null){
			return "redirect:/signin";
		}
		Activity activity=activityRepository.findOne(id);
		model.addAttribute("activity", activity);
		return "activity/applications";
	}
	
	@RequestMapping(value="/{id}/applications", method=RequestMethod.POST)
	public String viewPost(@PathVariable String id, Model model, 
			HttpServletRequest request, HttpSession session){
		return view(id, model, request, session);	
	}
	
	@RequestMapping(value="/{id}/application", method=RequestMethod.GET)
	public String app(@PathVariable String id, Model model, 
			HttpServletRequest request, HttpSession session){
		Activity activity=activityRepository.findOne(id);
		model.addAttribute("activity", activity);
		return "application/apply";
	}

}
