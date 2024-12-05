package com.macys.uop.foundation.core.utils.validation;

import javax.validation.groups.Default;

/**
 * It is a group construct that extends default Jakarta Bean Validation group 
 * <br>
 * It can be used in REST end points to enforce resource read(GET) related validations. 
 *
 */
public interface ReadGroup extends Default {
}
