package it.uniroma1.di.simulejos;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.SOURCE)
@Target({ ElementType.TYPE, ElementType.PACKAGE })
public @interface UpToDate {
}
