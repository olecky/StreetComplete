package de.westnordost.streetcomplete.quests.surface

import de.westnordost.streetcomplete.R
import de.westnordost.streetcomplete.data.meta.updateWithCheckDate
import de.westnordost.streetcomplete.data.osm.osmquest.SimpleOverpassQuestType
import de.westnordost.streetcomplete.data.osm.changes.StringMapChangesBuilder
import de.westnordost.streetcomplete.data.osm.mapdata.OverpassMapDataAndGeometryApi
import de.westnordost.streetcomplete.settings.ResurveyIntervalsStore

class AddFootwayPartSurface(o: OverpassMapDataAndGeometryApi, r: ResurveyIntervalsStore)
    : SimpleOverpassQuestType<DetailSurfaceAnswer>(o) {

    override val tagFilters = """
        ways with
        (
          highway = footway
          or (highway ~ path|cycleway|bridleway and foot != no)
        )
        and segregated = yes
        and (
            !footway:surface or
            footway:surface older today -${r * 8} years
            or
                (
                footway:surface ~ paved|unpaved
                and !footway:surface:note
                )
            )
    """
    override val commitMessage = "Add path surfaces"
    override val wikiLink = "Key:surface"
    override val icon = R.drawable.ic_quest_footway_surface
    override val isSplitWayEnabled = true

    override fun getTitle(tags: Map<String, String>) = R.string.quest_footwayPartSurface_title

    override fun createForm() = AddPathSurfaceForm()

    override fun applyAnswerTo(answer: DetailSurfaceAnswer, changes: StringMapChangesBuilder) {
        when(answer) {
            is SurfaceAnswer -> {
                changes.updateWithCheckDate("footway:surface", answer.value)
                changes.deleteIfExists("source:footway:surface")
                changes.deleteIfExists("footway:surface:note")
            }
            is DetailingWhyOnlyGeneric -> {
                changes.updateWithCheckDate("footway:surface", answer.value)
                changes.add("footway:surface:note", answer.note)
            }
        }
    }
}
