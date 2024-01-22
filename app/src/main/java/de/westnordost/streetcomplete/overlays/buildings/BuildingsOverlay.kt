package de.westnordost.streetcomplete.overlays.buildings

import de.westnordost.streetcomplete.R
import de.westnordost.streetcomplete.data.osm.mapdata.Element
import de.westnordost.streetcomplete.data.osm.mapdata.MapDataWithGeometry
import de.westnordost.streetcomplete.data.osm.mapdata.filter
import de.westnordost.streetcomplete.overlays.Overlay
import de.westnordost.streetcomplete.data.user.achievements.EditTypeAchievement.BUILDING
import de.westnordost.streetcomplete.overlays.AbstractOverlayForm
import de.westnordost.streetcomplete.overlays.Color
import de.westnordost.streetcomplete.overlays.PolygonStyle
import de.westnordost.streetcomplete.quests.building_type.AddBuildingType
import de.westnordost.streetcomplete.osm.building.BuildingType
import de.westnordost.streetcomplete.osm.building.BuildingType.*
import de.westnordost.streetcomplete.osm.building.createBuildingType
import de.westnordost.streetcomplete.osm.building.iconResName

class BuildingsOverlay : Overlay {

    override val title = R.string.overlay_buildings
    override val icon = R.drawable.ic_quest_building
    override val changesetComment = "Survey buildings"
    override val wikiLink = "Key:building"
    override val achievements = listOf(BUILDING)
    override val hidesQuestTypes = setOf(AddBuildingType::class.simpleName!!)

    override fun getStyledElements(mapData: MapDataWithGeometry) = mapData.filter(
        """
            ways, relations with
              building and building !~ no|entrance
              or man_made ~ communications_tower|tower|lighthouse|chimney|silo|storage_tank|water_tower|gasometer|cooling_tower
        """)
        .map {
            val building = createBuildingType(it.tags)
            it to PolygonStyle(color = building.color, icon = building?.iconResName)
        }

    override fun createForm(element: Element?) = BuildingsOverlayForm()

    private val BuildingType?.color get() = when(this) {
        // ~detached homes
        DETACHED, SEMI_DETACHED, HOUSEBOAT, BUNGALOW, STATIC_CARAVAN, HUT, FARM, -> // 10%
            Color.BLUE

        // ~non-detached homes
        HOUSE, DORMITORY, APARTMENTS, TERRACE, -> // 52%
            Color.SKY

        // unspecified residential
        RESIDENTIAL, -> // 12%
            Color.CYAN

        // parking, sheds, outbuildings in general...
        OUTBUILDING, CARPORT, GARAGE, GARAGES, SHED, BOATHOUSE, SERVICE, ALLOTMENT_HOUSE,
        TENT, CONTAINER, GUARDHOUSE, -> // 11%
            Color.LIME

        // commercial, industrial, farm buildings
        COMMERCIAL, KIOSK, RETAIL, OFFICE, BRIDGE, HOTEL, PARKING,
        INDUSTRIAL, WAREHOUSE, HANGAR, STORAGE_TANK,
        FARM_AUXILIARY, SILO, GREENHOUSE,
        ROOF -> // 5%
            Color.GOLD

        // amenity buildings
        TRAIN_STATION, TRANSPORTATION,
        CIVIC, GOVERNMENT, FIRE_STATION, HOSPITAL,
        KINDERGARTEN, SCHOOL, COLLEGE, UNIVERSITY, SPORTS_CENTRE, STADIUM, GRANDSTAND,
        RELIGIOUS, CHURCH, CHAPEL, CATHEDRAL, MOSQUE, TEMPLE, PAGODA, SYNAGOGUE, SHRINE,
        TOILETS, -> // 2%
            Color.ORANGE

        // other/special
        HISTORIC, ABANDONED, RUINS, CONSTRUCTION, BUNKER, TOMB,
        UNSUPPORTED ->
            Color.BLACK

        null -> Color.INVISIBLE
        // TODO Color.RED if not set && not something like military=yes / emergency=yes etc.?
    }
}
