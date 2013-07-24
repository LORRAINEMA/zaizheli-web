package net.zaizheli.web.mvc.controllers;

import java.text.ParseException;
import java.util.Date;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import net.zaizheli.constants.ActionType;
import net.zaizheli.constants.AjaxResultCode;
import net.zaizheli.domains.Action;
import net.zaizheli.domains.Activity;
import net.zaizheli.domains.CityMeta;
import net.zaizheli.domains.Place;
import net.zaizheli.domains.Resource;
import net.zaizheli.domains.User;
import net.zaizheli.repositories.ActionRepository;
import net.zaizheli.repositories.ActivityRepository;
import net.zaizheli.repositories.CityMetaRepository;
import net.zaizheli.repositories.PlaceRepository;
import net.zaizheli.repositories.ResourceRepository;
import net.zaizheli.repositories.UserRepository;
import net.zaizheli.services.ImageService;
import net.zaizheli.vo.ActivityCreationVo;
import net.zaizheli.vo.AjaxResult;
import net.zaizheli.vo.BindingErrors;
import net.zaizheli.vo.ImageReadyVo;
import net.zaizheli.vo.ValidationEngineError;
import net.zaizheli.web.utils.SessionUtil;
import net.zaizheli.web.utils.WebImageUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/activity")
public class EditActivityController {
		@Autowired
		ActionRepository actionRepository;
		@Autowired
		CityMetaRepository cityMetaRepository;
		@Autowired
		PlaceRepository placeRepository;
		@Autowired
		ResourceRepository resourceRepository;
		@Autowired
		ActivityRepository activityRepository;
		@Autowired
		UserRepository userRepository;
		@Autowired
		ImageService imageService;
		@Autowired
		WebImageUtil webImageUtil;
		@Autowired
		SessionUtil sessionUtil;
		
		@ModelAttribute("activityCreationVo")
		public ActivityCreationVo creatActivityCreationVo() {
			ActivityCreationVo vo = new ActivityCreationVo();
			return vo;
		}
		
		@RequestMapping(value="/{id}/edit", method=RequestMethod.GET)
		public String edit(@PathVariable String id, Model model, HttpSession session){
			if (sessionUtil.getSignInUser(session) == null) {
				return "redirect:/signin";
			}
			Activity activity=activityRepository.findOne(id);
			model.addAttribute("activity",activity);
			return "activity/edit";
		}
		
		@RequestMapping(value="/{id}/edit/validate", method=RequestMethod.POST)
		public @ResponseBody Object[] createValidate(@PathVariable String id, 
				@Valid ActivityCreationVo vo,BindingResult result, 
				ModelAndView mav, HttpSession session) {
			Activity activity=activityRepository.findOne(id);
			String start,end;
			if(vo.getStartDate()!=null && !vo.getStartDate().equals("")){
				start=vo.getStartDate();				
			}
			else {
				start=activity.getStartDate();
			}
			if(vo.getStartTime()!=null && !vo.getStartTime().equals("")){
				start+=vo.getStartTime();				
			}
			else {
				start+=activity.getStartTime();
			}
			if(vo.getEndDate()!=null && !vo.getEndDate().equals("")){
				end=vo.getEndDate();
			}
			else {
				end=activity.getEndDate();
			}
			if(vo.getEndTime()!=null && !vo.getEndTime().equals("")){
				end+=vo.getEndTime();
			}
			else {
				end+=activity.getEndTime();
			}
			if(start.compareTo(end)>0) {
				result.addError(new FieldError("vo", "endTime",
						"时间不对吧，再仔细想想吧~"));
			}
			if (result.hasErrors()) {
				result.addError(new FieldError("vo", "err",
						"信息有误，去前面看看吧~"));
				return ValidationEngineError.normalize(ValidationEngineError
						.from(result));
			} else {
				return new ValidationEngineError[] {};
			}
		}
		
		@RequestMapping(value="/{id}/edit", method=RequestMethod.POST)
		public @ResponseBody AjaxResult create(@PathVariable String id,
				@Valid ActivityCreationVo vo, BindingResult result, 
				ModelAndView mav, HttpSession session) throws ParseException{
			Activity activity=activityRepository.findOne(id);
			User signInUser = sessionUtil.getSignInUser(session);
			if(signInUser==null){
				return new AjaxResult(AjaxResultCode.NEED_SIGNIN);
			}
			String start,end;
			if(vo.getStartDate()!=null && !vo.getStartDate().equals("")){
				start=vo.getStartDate();				
			}
			else {
				start=activity.getStartDate();
			}
			if(vo.getStartTime()!=null && !vo.getStartTime().equals("")){
				start+=vo.getStartTime();				
			}
			else {
				start+=activity.getStartTime();
			}
			if(vo.getEndDate()!=null && !vo.getEndDate().equals("")){
				end=vo.getEndDate();
			}
			else {
				end=activity.getEndDate();
			}
			if(vo.getEndTime()!=null && !vo.getEndTime().equals("")){
				end+=vo.getEndTime();
			}
			else {
				end+=activity.getEndTime();
			}
			if(start.compareTo(end)>0) {
				result.addError(new FieldError("vo", "endTime",
						"时间不对吧，再仔细想想吧~"));
			}		
			if(result.hasErrors()){
				result.addError(new FieldError("vo", "err",
						"前面的信息有误，去看看吧~"));
				return new AjaxResult(AjaxResultCode.INVALID, 
						BindingErrors.from(result));
			}
			if(vo.getImageUrl()!=null && !vo.getImageUrl().equalsIgnoreCase("http://placehold.it/300&text=Upload+Image")) {
				try {
					// get image
					ImageReadyVo ir = webImageUtil
							.prepareImageFromUrl(vo.getImageUrl());
					String resId = imageService.put(ir.getFile());
					Resource res = new Resource();
					res.setOrgSize(ir.getOrgSize());
					res.setResId(resId);
					res.setExt(ir.getExt());
					resourceRepository.save(res);
					resourceRepository.delete(activity.getImage());
					activity.setImage(res);
				} catch (Exception e) {
					throw new RuntimeException(e.getMessage(), e);
				}
			}
			if(StringUtils.hasText(vo.getPlaceId())){
				Place place = placeRepository.findOne(vo.getPlaceId());
				if(place!=null){
					CityMeta city = cityMetaRepository.getByName(place.getCity());
					if(city!=null){
						activity.setCity(city.getPinyin());
					}
					activity.setLngLat(place.getLngLat());
					activity.setPlace(place);
				}
			}
			if(vo.getTitle()!=null && !vo.getTitle().equals("")) {
				activity.setTitle(vo.getTitle());
			}
			if(vo.getType()!=null && !vo.getType().equals("")) {
				activity.setType(vo.getType());
			}
			activity.setMaxNum(vo.getNum());
			activity.setStatus(vo.getStatus());	
			activity.setAddr(vo.getAddr());	
			activity.setFee(vo.getFee());	
			activity.setApply(vo.getApply());	
			activity.setContent(vo.getEditor1());
			activity.setUpdatedAt(new Date());
			activityRepository.save(activity);
			// save action
			Action action = new Action();
			action.setOwner(signInUser.getId());
			action.setCreatedAt(new Date());
			action.setTargetActivity(activity.getId());
			action.setType(ActionType.SETTING);
			action.setBy(sessionUtil.getBy(session));
			actionRepository.save(action);
			
			return new AjaxResult(AjaxResultCode.SUCCESS);
		}
	}

