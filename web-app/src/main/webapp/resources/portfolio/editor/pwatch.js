
(function(mod) {
  if (typeof exports == "object" && typeof module == "object") // CommonJS
    mod(require("../../lib/codemirror"));
  else if (typeof define == "function" && define.amd) // AMD
    define(["../../lib/codemirror"], mod);
  else // Plain browser env
    mod(CodeMirror);
})(function(CodeMirror) {
	"use strict";

	CodeMirror.defineMode("pwatch", function(config, parserConfig) {
	  "use strict";
	  
	  return {
	    token: function(stream, state) {
	    },
	    
	    startState: function() {
	      return {
	        pair: false,
	        pairStart: false,
	        keyCol: 0,
	        inlinePairs: 0,
	        inlineList: 0,
	        literal: false,
	        escaped: false
	      };
	    }
	  };
	});
});
