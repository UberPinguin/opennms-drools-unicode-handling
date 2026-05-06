package org.opennms.netmgt.correlation.drools;

import static org.awaitility.Awaitility.await;
import static org.junit.Assert.*;

import com.google.common.base.Strings;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.opennms.netmgt.model.events.EventBuilder;
import org.opennms.netmgt.xml.event.Event;
import org.opennms.netmgt.xml.event.Parm;

import java.util.concurrent.TimeUnit;

public class UnicodeHandlingTest extends CorrelationRulesTestCase {

    private static DroolsCorrelationEngine m_engine;
    private static TrackingAgendaEventListener m_taeListener;

    @Before
    public void setUp() {
        getAnticipator().reset();
        m_engine = findEngineByName("unicode-handling-tests");
        m_taeListener = new TrackingAgendaEventListener();
        m_engine.getKieSession().addEventListener(m_taeListener);
    }

    @AfterClass
    public static void tearDown() {
        m_engine.getKieSession().halt();
        m_engine.getKieSession().dispose();
        m_engine.getKieSession().destroy();
    }

    @Test
    public void testUnicodeHandlingEventhost() {
        Event e = new EventBuilder("uei.opennmms.org/tests/unicode/eventhost", "jUnit")
                .setHost("✅�").getEvent();
        m_engine.correlate(e);
        await().atMost(1500, TimeUnit.MILLISECONDS).untilAsserted(() -> assertTrue(m_taeListener
                .getFiredRuleNames().contains("event.host")));
        Event droolsEvent = (Event) m_engine.getKieSession().getObjects(Event.class::isInstance).stream()
                .filter((o -> !Strings.isNullOrEmpty(((Event) o).getHost()))).findFirst().get();
        assertEquals("✅�", droolsEvent.getHost());
    }

    @Test
    public void testUnicodeHandlingEventParm() {
        Event e = new EventBuilder("uei.opennmms.org/tests/unicode/eventparm", "jUnit")
                .addParam(new Parm("unicode_parm","✅�")).getEvent();
        m_engine.correlate(e);
        await().atMost(1500, TimeUnit.MILLISECONDS).untilAsserted(() -> assertTrue(m_taeListener
                .getFiredRuleNames().contains("eventparm unicode_parm")));
        Event droolsEvent = (Event) m_engine.getKieSession().getObjects(Event.class::isInstance).stream()
                .filter((o -> !Strings.isNullOrEmpty(((Event) o).getParm("processed_parm")
                        .getParmName()))).findFirst().get();
        assertEquals("✅�", droolsEvent.getParm("unicode_parm").getValue().getContent());
        assertEquals("✅�", droolsEvent.getParm("processed_parm").getValue().getContent());
    }

    @Test
    public void testUnicodeHandlingEventParmWithSerialization() {
        Event e = new EventBuilder("uei.opennmms.org/tests/unicode/eventparm/serialize", "jUnit")
                .addParam(new Parm("unicode_parm","✅�")).getEvent();
        m_engine.correlate(e);
        await().atMost(1500, TimeUnit.MILLISECONDS).untilAsserted(() -> assertTrue(m_taeListener
                .getFiredRuleNames().contains("eventparm unicode_parm serialize")));
        Event droolsEvent = (Event) m_engine.getKieSession().getObjects(Event.class::isInstance).stream()
                .filter((o -> !Strings.isNullOrEmpty(((Event) o).getParm("processed_parm")
                        .getParmName()))).findFirst().get();
        assertEquals("✅�", droolsEvent.getParm("unicode_parm").getValue().getContent());
        assertEquals("✅�", droolsEvent.getParm("processed_parm").getValue().getContent());
    }
}