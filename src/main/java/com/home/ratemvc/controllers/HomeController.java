package com.home.ratemvc.controllers;


import java.util.*;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import com.home.ratemvc.models.HomeModel;
import com.home.ratemvc.objects.CurrencyService;
import java.text.SimpleDateFormat;
import com.home.ratemvc.objects.Currency;


/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController {
	CurrencyService currencyService = new CurrencyService();
	
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	
	private void ViewModalSettingsCommonPart(ModelAndView model) {
		model.addObject("currencies", currencyService.getUserCurrenciesEntries());
		model.addObject("userCurrenciesCodes", currencyService.getUserCurrenciesCodes());		
		model.addObject("baseCurrencies", CurrencyService.getBaseCurrencies());
		model.addObject("availableCurrenciesSet", currencyService.getAvailableCurrencies());
	}
	
	// On start page
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ModelAndView home() {
		Date currentDate = new Date();		
		String dateFrom = "01" + (new SimpleDateFormat(".MM.yyyy")).format(currentDate);
		String dateTo   = (new SimpleDateFormat("dd.MM.yyyy")).format(currentDate);
		
		HomeModel homeModel = new HomeModel(dateFrom, dateTo);
		homeModel.setSelectedBaseCurrency("RUR");
		ModelAndView model = new ModelAndView("home", "homeModel", homeModel);
		ViewModalSettingsCommonPart(model);
		return model;
	}

	// Add currency
	@RequestMapping(value = "/", params="action_1", method = RequestMethod.POST)
	public ModelAndView AddButton(@ModelAttribute("homeModel") HomeModel homeModel) {
		ModelAndView model = new ModelAndView("home", "homeModel", homeModel);
		currencyService.AddCurrency(homeModel.getCurrency());
		ViewModalSettingsCommonPart(model);
		return model;
	}
	
	// Show description
	@RequestMapping(value = "/", params="action_3", method = RequestMethod.POST)
	public ModelAndView ShowDescriptionButton(@ModelAttribute("homeModel") HomeModel homeModel) {
		ModelAndView model = new ModelAndView("home", "homeModel", homeModel);
		String code = homeModel.getCurrencyEdit().getCode();
		homeModel.setCurrencyEdit(new Currency(code, currencyService.GetUserCurrencyDescription(code)));
		ViewModalSettingsCommonPart(model);
		return model;
	}
	
	// Save currency
	@RequestMapping(value = "/", params="action_4", method = RequestMethod.POST)
	public ModelAndView SaveButton(@ModelAttribute("homeModel") HomeModel homeModel) {
		ModelAndView model = new ModelAndView("home", "homeModel", homeModel);
		currencyService.AddCurrency(homeModel.getCurrencyEdit());
		ViewModalSettingsCommonPart(model);
		return model;
	}

	// Delete currency
	@RequestMapping(value = "/", params="action_2", method = RequestMethod.POST)
	public ModelAndView DeleteButton(@ModelAttribute("homeModel") HomeModel homeModel) {
		ModelAndView model = new ModelAndView("home", "homeModel", homeModel);
		currencyService.DeleteCurrency(homeModel.getCurrencyEdit().getCode());
		ViewModalSettingsCommonPart(model);
		return model;
	}
	
	// Request currency rate
	@RequestMapping(value = "/", params="action_5", method = RequestMethod.POST)
	public ModelAndView RequestRateButton(@Valid @ModelAttribute("homeModel") HomeModel homeModel, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			logger.debug("RequestRateButton: Validation error");
		}
		ModelAndView model = new ModelAndView("home", "homeModel", homeModel);
		ViewModalSettingsCommonPart(model);
		logger.debug("RequestRateButton: " + homeModel.getSelectedUserCurrency() + ", " + homeModel.getSelectedBaseCurrency());
		model.addObject("rates", currencyService.GetRatesForPeriod(homeModel.getSelectedUserCurrency(), homeModel.getSelectedBaseCurrency(), homeModel.getDateFrom(), homeModel.getDateTo()));
		return model;
	}

	// RESTFul interface implementation
	// RESTFul add currency 
	@RequestMapping(value = "/add/{code}/{description}", method = RequestMethod.GET)
	public ModelAndView AddCurrencyREST(@ModelAttribute("homeModel") HomeModel homeModel, @PathVariable String code, @PathVariable String description) {
		ModelAndView model = new ModelAndView("home", "homeModel", homeModel);
		currencyService.AddCurrency(new Currency(code.toUpperCase(), description));
		ViewModalSettingsCommonPart(model);
		return model;
	}
	
	// RESTFul delete currency
	@RequestMapping(value = "/delete/{code}", method = RequestMethod.GET)
	public ModelAndView DeleteREST(@ModelAttribute("homeModel") HomeModel homeModel, @PathVariable String code) {
		ModelAndView model = new ModelAndView("home", "homeModel", homeModel);
		currencyService.DeleteCurrency(code.toUpperCase());
		ViewModalSettingsCommonPart(model);
		return model;
	}
	
	// RESTFul request currency rate
	@RequestMapping(value = "/rates/{datefrom}/{dateto}/{usercode}/{basecode}", method = RequestMethod.GET)
	public ModelAndView RequestRateREST(@ModelAttribute("homeModel") HomeModel homeModel, @PathVariable String datefrom, @PathVariable String dateto, @PathVariable String usercode, @PathVariable String basecode) {
		ModelAndView model = new ModelAndView("home", "homeModel", homeModel);
		ViewModalSettingsCommonPart(model);
		logger.debug("RequestRateREST: " + usercode + ", " + basecode);
		model.addObject("rates", currencyService.GetRatesForPeriod(usercode.toUpperCase(), basecode.toUpperCase(), datefrom, dateto));
		return model;
	}
}
