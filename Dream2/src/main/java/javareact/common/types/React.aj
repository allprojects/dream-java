/*package javareact.common.types;

import java.lang.reflect.Method;

import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;

import javareact.common.packets.content.Attribute;

public aspect React {
  
  pointcut hasReactiveAnnotation(Object o):
    target(o) && execution(@javareact.common.types.ImpactsOn * *(..));

  after(Object o): hasReactiveAnnotation(o) {
    Signature sig = thisJoinPoint.getSignature();
    assert (sig instanceof MethodSignature);
    MethodSignature methodSig = (MethodSignature) sig;
    Method method = methodSig.getMethod();
    ImpactsOn annotation = method.getAnnotation(ImpactsOn.class);
    String[] impactsOnMethods = annotation.methods();
    
    Attribute[] attributes = new Attribute[impactsOnMethods.length];
    int i=0;
    for (String impactsOnMethod : impactsOnMethods) {
    try {
      Method methodToInvoke = o.getClass().getMethod(impactsOnMethod);
      Object retVal = methodToInvoke.invoke(o);
      Attribute attr = new Attribute(impactsOnMethod, retVal);
      attributes[i++] = attr;
    } catch (Exception e) {
      e.printStackTrace();
    }
    ((Observable) o).sendEvent(attributes);
    }
  }
  
}*/