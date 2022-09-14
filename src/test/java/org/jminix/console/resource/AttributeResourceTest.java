package org.jminix.console.resource;

import static org.junit.Assert.assertEquals;

import java.lang.management.ManagementFactory;
import java.util.Map;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.jminix.console.JMiniXStuff;
import org.jminix.server.DefaultLocalServerConnectionProvider;
import org.jminix.type.AttributeFilter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.resource.Resource;

public class AttributeResourceTest {
	private MBeanServer mbs;
	private ObjectName mbeanName;

	@Before
	public void setup() throws Exception {
        mbs = ManagementFactory.getPlatformMBeanServer();
        mbeanName = new ObjectName("org.jminix.console:type=JMiniXStuff");
        mbs.registerMBean(new JMiniXStuff(), mbeanName);
	}

	@After
	public void teardown() throws Exception {
		mbs.unregisterMBean(mbeanName);
	}

	@Test
	public void testPrimitiveAttribute() {
		AttributeResource res = new AttributeResource();
		init(res);
		Request req = res.getRequest();
		req.getAttributes().put("attribute", "SimpleString");
		Map<String,Object> model = res.getModel();
		assertEquals(JMiniXStuff.SIMPLE_STRING_VALUE, model.get(AbstractTemplateResource.VALUE_MODEL_ATTRIBUTE));
	}

	@Test
	public void testArrayAttribute() {
		AttributeResource res = new AttributeResource();
		init(res);
		Request req = res.getRequest();
		req.getAttributes().put("attribute", "StringArray");
		Map<String,Object> model = res.getModel();
		assertEquals(JMiniXStuff.STRING_ARRAY_VALUE, model.get(AbstractTemplateResource.ITEMS_MODEL_ATTRIBUTE));
	}

	@Test
	public void testArrayFilter() {
		AttributeResource res = new AttributeResource();
		init(res, new AttributeFilter() {
			@Override
			public Object filter(Object object) {
				return String.join(" ", (String[])object);
			}
		});
		Request req = res.getRequest();
		req.getAttributes().put("attribute", "StringArray");
		Map<String,Object> model = res.getModel();
		assertEquals(JMiniXStuff.SIMPLE_STRING_VALUE, model.get(AbstractTemplateResource.VALUE_MODEL_ATTRIBUTE));
	}

	private void init(Resource res) {
		init(res, null);
	}

	private void init(Resource res, AttributeFilter attributeFilter) {
		Context ctx = new Context();
		ctx.getAttributes().put("serverProvider", new DefaultLocalServerConnectionProvider());
		if (attributeFilter != null) {
			ctx.getAttributes().put("attributeFilter", attributeFilter);
		}
		Request req = new Request();
		req.getAttributes().put("domain", "org.jminix.console");
		req.getAttributes().put("mbean", "type=JMiniXStuff");
		req.getAttributes().put("server", "0");
		Response resp = new Response(req);
		res.init(ctx, req, resp);
	}
}
