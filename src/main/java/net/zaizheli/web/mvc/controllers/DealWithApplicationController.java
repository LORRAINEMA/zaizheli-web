package net.zaizheli.web.mvc.controllers;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import net.zaizheli.constants.ActionType;
import net.zaizheli.constants.AjaxResultCode;
import net.zaizheli.constants.ApplicationStatus;
import net.zaizheli.domains.Action;
import net.zaizheli.domains.Activity;
import net.zaizheli.domains.Application;
import net.zaizheli.domains.Join;
import net.zaizheli.repositories.ActionRepository;
import net.zaizheli.repositories.ActivityRepository;
import net.zaizheli.repositories.ApplicationRepository;
import net.zaizheli.repositories.JoinRepository;
import net.zaizheli.vo.AjaxResult;
import net.zaizheli.web.utils.SessionUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/application")
public class DealWithApplicationController {
	
	@Autowired
	ActivityRepository activityRepository;
	@Autowired 
	SessionUtil sessionUtil;
	@Autowired
	JoinRepository joinRepository;
	@Autowired
	ApplicationRepository applicationRepository;
	@Autowired
	ActionRepository actionRepository;
	
	@RequestMapping(value="/{id}/agree", method=RequestMethod.GET)
	public @ResponseBody AjaxResult agree(@PathVariable String id, Model model, 
			HttpServletRequest request, HttpSession session){
		Application application=applicationRepository.findOne(id);
		Activity activity = application.getActivity();
		if(application.getStatus() == ApplicationStatus.申请中) {	
			if(activity.getCurrentNum() >= activity.getMaxNum()) {
				return new AjaxResult(AjaxResultCode.INVALID, "不能再加了 -o- 已达到最大人数上限");
			}
			activityRepository.inc(activity.getId(),"inJudgingCount",-1);
			activityRepository.inc(activity.getId(),"currentNum",1);
			application.setStatus(ApplicationStatus.已加入);
			applicationRepository.save(application);
			Join join = new Join();
			join.setActivity(activity);
			join.setCreatedAt(new Date());
			join.setJoiner(application.getApplicant());
			join.setStatus(2);
			joinRepository.save(join);
			// save action
			Action action = new Action();
			action.setOwner(application.getApplicant().getId());
			action.setCreatedAt(new Date());
			action.setTargetActivity(activity.getId());
			action.setType(ActionType.JOIN);
			action.setBy(sessionUtil.getBy(session));
			actionRepository.save(action);
			return new AjaxResult(AjaxResultCode.SUCCESS,activity.getId());
		}
		else {
			return new AjaxResult(AjaxResultCode.INVALID,"此人已不在申请行列中了");
		}
	}
	
	@RequestMapping(value="/{id}/refuse", method=RequestMethod.GET)
	public @ResponseBody AjaxResult refuse(@PathVariable String id, Model model, 
			HttpServletRequest request, HttpSession session){
		Application application=applicationRepository.findOne(id);
		Activity activity = application.getActivity();
		if(application.getStatus() == ApplicationStatus.申请中) {
			activityRepository.inc(activity.getId(),"inJudgingCount",-1);
			application.setStatus(ApplicationStatus.拒绝);
			applicationRepository.save(application);
			return new AjaxResult(AjaxResultCode.SUCCESS, activity.getId());
		}
		else {
			return new AjaxResult(AjaxResultCode.INVALID,"此人已不在申请行列中了");
		}
	}
	
	@RequestMapping(value="/{id}/kickout", method=RequestMethod.GET)
	public @ResponseBody AjaxResult kickout(@PathVariable String id, Model model, 
			HttpServletRequest request, HttpSession session){
		Application application=applicationRepository.findOne(id);
		Activity activity = application.getActivity();
		if(application.getStatus() == ApplicationStatus.已加入) {
			activityRepository.inc(activity.getId(),"applicationCount",-1);
			activityRepository.inc(activity.getId(),"currentNum",-1);
			Join join = joinRepository.findByActivityAndjoiner(activity.getId(),application.getApplicant().getId());
			joinRepository.delete(join);
			applicationRepository.delete(application);
			return new AjaxResult(AjaxResultCode.SUCCESS, activity.getId());
		}
		else {
			return new AjaxResult(AjaxResultCode.INVALID,"此人已不在活动成员行列中了");
		}
	}

}
