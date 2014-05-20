package fr.acxio.tools.agia;

/*
 * Copyright 2014 Acxio
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;

public class SpringTestUtils {

	// http://kim.saabye-pedersen.org/2012/12/mockito-and-spring-proxies.html
	public static final Object unwrapProxy(Object bean) throws Exception {
		/*
		 * If the given object is a proxy, set the return value as the object
		 * being proxied, otherwise return the given object.
		 */
		if (AopUtils.isAopProxy(bean) && bean instanceof Advised) {
			Advised advised = (Advised) bean;
			bean = advised.getTargetSource().getTarget();
		}
		return bean;
	}
}
