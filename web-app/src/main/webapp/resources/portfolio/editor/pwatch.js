
(function(mod) {
  if (typeof exports == "object" && typeof module == "object") // CommonJS
    mod(require("../../lib/codemirror"));
  else if (typeof define == "function" && define.amd) // AMD
    define(["../../lib/codemirror"], mod);
  else // Plain browser env
    mod(CodeMirror);
})(function(CodeMirror) {
	"use strict";
	
	var attributes = ['PRICE', 'MARKET_CAP', 'ENTERPRISE_VALUE', 'TRAILING_PE', 'FORWARD_PE', 'PEG_RATIO', 'PRICE_SALES', 'PRICE_BOOK', 'ENTERPRISE_VALUE_REVENUE', 'ENTERPRISE_VALUE_EBITDA', 'PROFIT_MARGIN', 'OPERATING_MARGIN', 'RETURN_ON_ASSETS', 'RETURN_ON_EQUITY', 'REVENUE', 'REVENUE_PER_SHARE', 'QTRLY_REVENUE_GROWTH', 'GROSS_PROFIT', 'EBITDA', 'NET_INCOME_AVL_TO_COMMON', 'DILUTED_EPS', 'QTRLY_EARNINGS_GROWTH', 'TOTAL_CASH', 'TOTAL_CASH_PER_SHARE', 'TOTAL_DEBT', 'TOTAL_DEBT_EQUITY', 'CURRENT_RATIO', 'BOOK_VALUE_PER_SHARE', 'OPERATING_CASH_FLOW', 'LEVERED_FREE_CASH_FLOW', 'BETA', '52_WEEK_CHANGE', '52_WEEK_HIGH', '52_WEEK_LOW', '50_DAY_MOVING_AVERAGE', '200_DAY_MOVING_AVERAGE', 'AVG_VOL_3_MONTH', 'AVG_VOL_10_DAY', 'SHARES_OUTSTANDING', 'FLOAT', 'PERCENTAGE_HELD_BY_INSIDERS', 'PERCENTAGE_HELD_BY_INSTITUTIONS', 'SHARES_SHORT_CURRENT_MONTH', 'SHORT_RATIO', 'SHORT_PERCENTAGE_OF_FLOAT', 'SHARES_SHORT_PRIOR_MONTH', 'FORWARD_ANNUAL_DIVIDEND_RATE', 'FORWARD_ANNUAL_DIVIDEND_YIELD', 'TRAILING_ANNUAL_DIVIDEND_YIELD_ABSOLUTE', 'TRAILING_ANNUAL_DIVIDEND_YIELD_RELATIVE', '5_YEAR_AVERAGE_DIVIDEND_YIELD', 'PAYOUT_RATIO', 'FISCAL_YEAR_ENDS', 'MOST_RECENT_QUARTER', 'DIVIDEND_DATE', 'EX_DIVIDEND_DATE'];
	var functions = ['SIN', 'COS', 'TAN', 'EXP', 'LOG', 'POW', 'SQRT'];
	var logical = ['AND', 'OR', 'NOT'];
	
	// Add the following if they are allowed
	//'COUNTRY', 'CURRENCY'
	
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
