package at.ac.tuwien.ase09.validator;

import java.math.BigDecimal;
import java.util.Calendar;

import at.ac.tuwien.ase09.model.order.OrderAction;
import at.ac.tuwien.ase09.model.order.OrderType;

public class OrderValidator {
	public static final int OK = 0;
	public static final int LIMIT_REQUIRED = 1;
	public static final int STOP_LIMIT_EQUAL_OR_LOWER_THAN_PRICE = 2;
	public static final int STOP_LIMIT_EQUAL_OR_HIGHER_THAN_PRICE = 3;
	public static final int LIMIT_LOWER_THAN_STOP_LIMIT = 4;
	public static final int LIMIT_HIGHER_THAN_STOP_LIMIT = 5;
	public static final int LIMIT_EQUAL_OR_HIGHER_THAN_PRICE = 6;
	public static final int LIMIT_EQUAL_OR_LOWER_THAN_PRICE = 7;
	
	public static final int VALID_FROM_REQUIRED = 8;
	public static final int VALID_TO_BEFORE_NOW = 9;
	public static final int VALID_TO_BEFORE_VALID_FROM = 10;
	
	public static int validateOrder(OrderType orderType, OrderAction orderAction, BigDecimal currentPrice, BigDecimal limit, BigDecimal stopLimit, Calendar validFrom, Calendar validTo){
		if(orderType == OrderType.LIMIT) {
            if(limit == null){
                return LIMIT_REQUIRED;
            }
            if(stopLimit != null){
                // we have a stop limit or a stop order
                if(orderAction == OrderAction.BUY){
                    // stop limit must be higher than the current price
                    if(stopLimit.compareTo(currentPrice) <= 0){
                        return STOP_LIMIT_EQUAL_OR_LOWER_THAN_PRICE;
                    }

                    // limit must be equal to or higher than the stop limit
                    if(limit.compareTo(stopLimit) < 0){
                        return LIMIT_LOWER_THAN_STOP_LIMIT;
                    }
                }else{
                    // stop limit must be lower than the current price
                    if(stopLimit.compareTo(currentPrice) >= 0){
                        return STOP_LIMIT_EQUAL_OR_HIGHER_THAN_PRICE;
                    }

                    // limit must be equal to or lower than the stop limit
                    if(limit.compareTo(stopLimit) > 0){
                        return LIMIT_HIGHER_THAN_STOP_LIMIT;
                    }
                }
            }else{
                // we have a limit order
                if(orderAction == OrderAction.BUY){
                    // limit must be lower than the current price
                    if(limit.compareTo(currentPrice) >= 0){
                        return LIMIT_EQUAL_OR_HIGHER_THAN_PRICE;
                    }
                }else{
                    // limit must be higher than the current price
                    if(limit.compareTo(currentPrice) <= 0){
                        return LIMIT_EQUAL_OR_LOWER_THAN_PRICE;
                    }
                }
            }
        }
		
		if(validFrom == null){
			return VALID_FROM_REQUIRED;
		}
		
		if(validTo != null){
			Calendar now = Calendar.getInstance();
			if(validTo.before(now)){
				// validTo must be after the current date
				return VALID_TO_BEFORE_NOW;
			}
			if(validTo.before(validFrom)){
				return VALID_TO_BEFORE_VALID_FROM;
			}
		}
        return OK;
	}
}
