package javareact.common.types.observable;

import java.lang.reflect.Method;

import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;

import javareact.common.packets.content.Attribute;

public aspect React {
  
  pointcut hasReactiveAnnotation(Object o):
    target(o) && execution(@javareact.common.types.observable.ImpactOn * *(..));

  after(Object o): hasReactiveAnnotation(o) {
    Signature sig = thisJoinPoint.getSignature();
    assert (sig instanceof MethodSignature);
    MethodSignature methodSig = (MethodSignature) sig;
    Method method = methodSig.getMethod();
    ImpactOn annotation = method.getAnnotation(ImpactOn.class);
    String[] impactOnMethods = annotation.method();
    
    Attribute[] attributes = new Attribute[impactOnMethods.length];
    int i=0;
    for (String impactOnMethod : impactOnMethods) {
    try {
      Method methodToInvoke = o.getClass().getMethod(impactOnMethod);
      Object retVal = methodToInvoke.invoke(o);
      Attribute attr = new Attribute(impactOnMethod + "()", retVal);
      attributes[i++] = attr;
    } catch (Exception e) {
      e.printStackTrace();
    }
    ((Observable) o).sendEvent(attributes);
    }
  }
  
}