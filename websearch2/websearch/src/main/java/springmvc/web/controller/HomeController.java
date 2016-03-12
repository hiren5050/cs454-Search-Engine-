package springmvc.web.controller;


import java.util.Map;

import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {

	

	@RequestMapping("/home.html")
	public String index(ModelMap model) throws ParseException {
		SearchText objSearch = new SearchText();
		model.put("terms", objSearch.jArray);
		return "home";
	}

	@RequestMapping(value = "/home.html", method = RequestMethod.POST)
	public String getResults(ModelMap model, @RequestParam(required = false) String txtsearch) throws ParseException {
		SearchText objSearch = new SearchText();
	    Map<String, Double> map =	objSearch.searchtext(txtsearch);
		
	    model.put("map",map);
	    
		return "home";
	}

}
