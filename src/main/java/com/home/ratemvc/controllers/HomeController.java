package com.home.ratemvc.controllers;


import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import com.home.ratemvc.models.HomeModel;
import com.home.ratemvc.objects.CurrencyService;
import static java.util.Arrays.*;


/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController {
	CurrencyService currencyService = new CurrencyService();
	
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	
	@RequestMapping(value="/processForm1",params="action1",method=RequestMethod.POST)
    public ModelAndView action1(@ModelAttribute("homeModel") HomeModel homeModel)
    {
        System.out.println("Action1 block called");
        ModelAndView model = new ModelAndView("home", "homeModel", homeModel);
        return model;
    }
    @RequestMapping(value="/processForm1",params="action2",method=RequestMethod.POST)
    public ModelAndView action2(@ModelAttribute("homeModel") HomeModel homeModel)
    {
        System.out.println("Action2 block called");
        ModelAndView model = new ModelAndView("home", "homeModel", homeModel);
        return model;
    }
    
	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ModelAndView home() {
		
		Calendar c = new GregorianCalendar(); 
		int day = c.get(Calendar.DAY_OF_MONTH); 
		int month = c.get(Calendar.MONTH); 
		int year = c.get(Calendar.YEAR);
		String date = String.valueOf(day) + "." + (month<10?"0":"") + String.valueOf(month) + "." + String.valueOf(year);
		String dateFrom = "01" + date;
		String dateTo   = (day<10?"0":"") + date;
		
		HomeModel homeModel = new HomeModel(dateFrom, dateTo);
		homeModel.setSelectedBaseCurrency("EUR");
		ModelAndView model = new ModelAndView("home", "homeModel", homeModel);
		ViewModalSettingsCommonPart(model);
		return model;
	}
	
	private void ViewModalSettingsCommonPart(ModelAndView model) {
		model.addObject("currencies", currencyService.getUserCurrencies());
		model.addObject("userCurrenciesCodes", currencyService.getUserCurrenciesCodes());		
		model.addObject("baseCurrencies", CurrencyService.getBaseCurrencies());
		model.addObject("availableCurrenciesSet", CurrencyService.getAvailableCurrenciesSet());
		
	}
	
	// Add value = "/edit-currency"
	@RequestMapping(value = "/", params="action_1", method = RequestMethod.POST)
	public ModelAndView EditCurrency(@ModelAttribute("homeModel") HomeModel homeModel) {
		System.out.println("Action_1");
		ModelAndView model = new ModelAndView("home", "homeModel", homeModel);
		System.out.println("AddCurrency: " + homeModel.getCurrency().getCode());		
		currencyService.AddCurrency(homeModel.getCurrency());     // homeModel.getSelectedAvailableCurrency()
		ViewModalSettingsCommonPart(model);
		return model;
	}

	// Show description
	@RequestMapping(value = "/", params="action_3", method = RequestMethod.POST)
	public ModelAndView ShowDescription(@ModelAttribute("homeModel") HomeModel homeModel) {
		System.out.println("Action_1");
		ModelAndView model = new ModelAndView("home", "homeModel", homeModel);
		System.out.println("Show Currency: " + homeModel.getCurrencyEdit().getCode());
		String code = homeModel.getCurrencyEdit().getCode();
		System.out.println(code + ", " + currencyService.getUserCurrencyByCode(code));
		homeModel.setCurrencyEdit(currencyService.getUserCurrencyByCode(code));
		ViewModalSettingsCommonPart(model);
		return model;
	}
	
	// Save
	@RequestMapping(value = "/", params="action_4", method = RequestMethod.POST)
	public ModelAndView Save(@ModelAttribute("homeModel") HomeModel homeModel) {
		System.out.println("Action_4");
		ModelAndView model = new ModelAndView("home", "homeModel", homeModel);
		System.out.println("SaveCurrency: " + homeModel.getCurrencyEdit().getCode() + ", " + homeModel.getCurrencyEdit().getDescription());		
		currencyService.AddCurrency(homeModel.getCurrencyEdit());
		ViewModalSettingsCommonPart(model);
		return model;
	}

	// Delete currency
	@RequestMapping(value = "/", params="action_2", method = RequestMethod.POST)
	public ModelAndView DeleteCurrency(@ModelAttribute("homeModel") HomeModel homeModel) {
		System.out.println("Action_2");
		ModelAndView model = new ModelAndView("home", "homeModel", homeModel);
		currencyService.DeleteCurrency(homeModel.getCurrencyEdit().getCode());
		ViewModalSettingsCommonPart(model);
		return model;
	}
	
	//value = "/request-rate"
	@RequestMapping(value = "/", params="action_5", method = RequestMethod.POST)
	public ModelAndView RequestRate(@ModelAttribute("homeModel") HomeModel homeModel) {
		System.out.println("Action_5");
		ModelAndView model = new ModelAndView("home", "homeModel", homeModel);
		ViewModalSettingsCommonPart(model);
		System.out.println("C:" + homeModel.getCurrency().getCode());
		model.addObject("rates", currencyService.GetRatesForPeriod(homeModel.getSelectedUserCurrency(), homeModel.getSelectedBaseCurrency(), homeModel.getDateFrom(), homeModel.getDateTo()));
		return model;
	}
	
}
