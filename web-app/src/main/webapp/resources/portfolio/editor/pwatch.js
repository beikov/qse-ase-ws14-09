
(function(mod) {
  if (typeof exports == "object" && typeof module == "object") // CommonJS
    mod(require("../../lib/codemirror"));
  else if (typeof define == "function" && define.amd) // AMD
    define(["../../lib/codemirror"], mod);
  else // Plain browser env
    mod(CodeMirror);
})(function(CodeMirror) {
	"use strict";
	
	var attributes = ['FISCALYEARENDS', 'MOSTRECENTQUARTER', 'DIVIDENDDATE', 'EXDIVIDENDDATE', 'MARKETCAP', 'ENTERPRISEVALUE', 'TRAILINGPE', 'FORWARDPE', 'PEGRATIO', 'PRICESALES', 'PRICEBOOK', 'ENTERPRISEVALUEREVENUE', 'ENTERPRISEVALUEEBITDA', 'PROFITMARGIN', 'OPERATINGMARGIN', 'RETURNONASSETS', 'RETURNONEQUITY', 'REVENUE', 'REVENUEPERSHARE', 'QTRLYREVENUEGROWTH', 'GROSSPROFIT', 'EBITDA', 'NETINCOMEAVLTOCOMMON', 'DILUTEDEPS', 'QTRLYEARNINGSGROWTH', 'TOTALCASH', 'TOTALCASHPERSHARE', 'TOTALDEBT', 'TOTALDEBTEQUITY', 'CURRENTRATIO', 'BOOKVALUEPERSHARE', 'OPERATINGCASHFLOW', 'LEVEREDFREECASHFLOW', 'BETA', 'P52WEEKCHANGE', 'SP50052WEEKCHANGE', 'P52WEEKHIGH', 'P52WEEKLOW', 'P50DAYMOVINGAVERAGE', 'P200DAYMOVINGAVERAGE', 'AVGVOL3MONTH', 'AVGVOL10DAY', 'SHARESOUTSTANDING', 'FLOATVAL', 'PERCENTAGEHELDBYINSIDERS', 'PERCENTAGEHELDBYINSTITUTIONS', 'SHARESSHORTCURRENTMONTH', 'SHORTRATIO', 'SHORTPERCENTAGEOFFLOAT', 'SHARESSHORTPRIORMONTH', 'FORWARDANNUALDIVIDENDRATE', 'FORWARDANNUALDIVIDENDYIELD', 'TRAILINGANNUALDIVIDENDYIELDABSOLUTE', 'TRAILINGANNUALDIVIDENDYIELDRELATIVE', 'P5YEARAVERAGEDIVIDENDYIELD', 'PAYOUTRATIO'];
	var functions = ['SIN', 'COS', 'TAN', 'EXP', 'LOG', 'POW', 'SQRT'];
	var logical = ['AND', 'OR', 'NOT'];
	
	var keywords = attributes.concat(functions).concat(logical);
	
	CodeMirror.defineSimpleMode("pwatch", {
	  // The start state contains the rules that are intially used
	  start: [
	    // Rules are matched in the order in which they appear, so there is
	    // no ambiguity between this one and the one above
	    {regex: new RegExp("(?:" + keywords.join("|") + ")\\b"),
	     token: "keyword"},
	    {regex: /[-+]?(?:\.\d+|\d+\.?\d*)(?:e[-+]?\d+)?/i,
	     token: "number"},
	    {regex: /[-+\/*=<>!]+/, token: "operator"},
	    // indent and dedent properties guide autoindentation
	    {regex: /[\(]/, indent: true},
	    {regex: /[\)]/, dedent: true},
	  ],
	});
	

	CodeMirror.registerHelper("hint", "pwatch", function(cm) {
		var cur = cm.getCursor(), token = cm.getTokenAt(cur);
		var word = token.string, start = token.start, end = token.end;
		
		var result = [];
		function add(elements) {
		  for (var i = 0; i < elements.length; i++)
		    //if (!word || elements[i].indexOf(word, 0) == 0)
		      result.push(elements[i]);
		}
		
		add(keywords);
		
		return {
	      list: result,
	      from: CodeMirror.Pos(cur.line, token.start),
	            to: CodeMirror.Pos(cur.line, token.end)
	    };
		
//		var cur = cm.getCursor(), token = cm.getTokenAt(cur);
//		var inner = CodeMirror.innerMode(cm.getMode(), token.state);
//		if (inner.mode.name != "css") return;
//		
//		var word = token.string, start = token.start, end = token.end;
//		if (/[^\w$_-]/.test(word)) {
//		  word = ""; start = end = cur.ch;
//		}
//		
//		var spec = CodeMirror.resolveMode("text/css");
//		
//		var result = [];
//		function add(keywords) {
//		  for (var name in keywords)
//		    if (!word || name.lastIndexOf(word, 0) == 0)
//		      result.push(name);
//		}
//		
//		var st = inner.state.state;
//		if (st == "pseudo" || token.type == "variable-3") {
//		  add(pseudoClasses);
//		} else if (st == "block" || st == "maybeprop") {
//		  add(spec.propertyKeywords);
//		} else if (st == "prop" || st == "parens" || st == "at" || st == "params") {
//		  add(spec.valueKeywords);
//		  add(spec.colorKeywords);
//		} else if (st == "media" || st == "media_parens") {
//		  add(spec.mediaTypes);
//		  add(spec.mediaFeatures);
//		}
//		
//		if (result.length) return {
//		  list: result,
//		  from: CodeMirror.Pos(cur.line, start),
//		  to: CodeMirror.Pos(cur.line, end)
//		};
	});
});
