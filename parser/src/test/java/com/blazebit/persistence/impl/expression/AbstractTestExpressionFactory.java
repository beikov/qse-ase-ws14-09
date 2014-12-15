/*
 * Copyright 2014 Blazebit.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.blazebit.persistence.impl.expression;

import com.blazebit.persistence.parser.JPQLSelectExpressionLexer;
import com.blazebit.persistence.parser.JPQLSelectExpressionParser;
import java.util.logging.Level;
import org.antlr.v4.runtime.BailErrorStrategy;
import org.antlr.v4.runtime.DiagnosticErrorListener;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.atn.PredictionMode;

/**
 *
 * @author Moritz Becker
 */
public abstract class AbstractTestExpressionFactory extends AbstractExpressionFactory {

    
    @Override
    protected void configureLexer(JPQLSelectExpressionLexer lexer) {
        lexer.removeErrorListeners();
        lexer.addErrorListener(ERR_LISTENER);
    }

    @Override
    protected void configureParser(JPQLSelectExpressionParser parser) {
        parser.setTrace(true);
        parser.removeErrorListeners();
        parser.addErrorListener(ERR_LISTENER);
        parser.getInterpreter().setPredictionMode(PredictionMode.LL_EXACT_AMBIG_DETECTION);
    }

}
