package javareact.common.types.observable;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ImpactOn {
  String[] method() default { "get()" };
}
