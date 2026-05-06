package org.opennms.netmgt.correlation.drools;

import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.DefaultAgendaEventListener;
import org.kie.api.runtime.rule.Match;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// from https://www.baeldung.com/drools-list-matched-rules#1-agendaeventlistener
// does what we want for now, no point in rewriting it from scratch.
public class TrackingAgendaEventListener extends DefaultAgendaEventListener {
    private final List<Match> matchList = Collections.synchronizedList(new ArrayList<>());

    @Override
    public void afterMatchFired(AfterMatchFiredEvent event) {
        matchList.add(event.getMatch());
    }

    public List<String> getFiredRuleNames() {
        List<String> names = new ArrayList<>();
        matchList.forEach(m -> names.add(m.getRule().getName()));
        return names;
    }

    public void clearFiredRuleNames() {
        matchList.clear();
    }

}