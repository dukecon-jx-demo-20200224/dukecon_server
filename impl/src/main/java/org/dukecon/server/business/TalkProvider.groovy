package org.dukecon.server.business

import com.fasterxml.jackson.databind.ObjectMapper
import groovy.json.JsonSlurper
import org.dukecon.model.Speaker
import org.dukecon.model.Talk
import org.springframework.stereotype.Component

/**
 * @author Niko Köbler, http://www.n-k.de, @dasniko
 */
@Component
class TalkProvider {

    private def talks = []

    def List<Talk> getAllTalks() {
        if (talks.isEmpty())
            readJavalandFile() as List<Talk>
        talks
    }

    private def readDemoFile() {
        def mapper = new ObjectMapper()
        def is = this.getClass().getResourceAsStream('/demotalks.json')
        def slurper = new JsonSlurper()
        def json = slurper.parse(is)
        json.each {
            talks.add(mapper.convertValue(it, Talk.class))
        }
    }

    private def readJavalandFile() {
        def javaland = new URL('https://www.javaland.eu/api/schedule/JavaLand2015/jl.php?key=TestJL')
        def slurper = new JsonSlurper()
        def json = slurper.parse(javaland)
        json.hits.hits.each {
            def t = it._source
            Speaker speaker = Speaker.builder().name(t.REFERENT_NAME).company(t.REFERENT_FIRMA).defaultSpeaker(true).build()
            Speaker speaker2 = t.COREFERENT_NAME == null ? null : Speaker.builder().name(t.COREFERENT_NAME).company(t.COREFERENT_FIRMA).build()
            Talk talk = Talk.builder()
                    .id(t.ID.toString())
                    .track(t.TRACK_EN)
                    .level(t.AUDIENCE_EN)
                    .type(t.VORTRAGSTYP_EN)
                    .start(t.DATUM_ES_EN + 'T' + t.BEGINN)
                    .end(t.DATUM_ES_EN + 'T' + t.ENDE)
                    .location(t.RAUMNAME)
                    .title(t.TITEL)
                    ._abstract(t.ABSTRACT_TEXT)
                    .language(t.SPRACHE)
                    .demo(t.DEMO != null && t.DEMO.equalsIgnoreCase('ja'))
                    .speakers([speaker, speaker2])
                    .build()
            talks.add(talk)
        }
    }

}