package nl.rvt.gatas.companion.services

import nl.rvantwisk.gatas.lib.extensions.MessageType
import nl.rvantwisk.gatas.lib.extensions.cobsEncode
import kotlin.test.Test
import kotlin.test.assertEquals

class RelayCycleDiagnosticsTest {
    @Test
    fun countsAircraftPositionFramesWithoutDecodingTheirPayload() {
        val response = byteArrayOf(MessageType.AIRCRAFT_POSITION_TYPE_V2.value.toByte(), 1, 2)
            .cobsEncode() +
            byteArrayOf(MessageType.AIRCRAFT_POSITION_TYPE_V1.value.toByte(), 3, 4)
                .cobsEncode() +
            byteArrayOf(MessageType.AIRCRAFT_CONFIGURATIONS_V2.value.toByte(), 5)
                .cobsEncode()

        assertEquals(
            RelayResponseSummary(aircraftPositions = 2, otherMessages = 1),
            summarizeRelayResponse(response),
        )
    }

    @Test
    fun reportsMalformedFramesAndContinuesWithFollowingFrames() {
        val malformed = byteArrayOf(5, 1, 0)
        val valid = byteArrayOf(MessageType.AIRCRAFT_POSITION_TYPE_V2.value.toByte()).cobsEncode()

        assertEquals(
            RelayResponseSummary(aircraftPositions = 1, malformedFrames = 1),
            summarizeRelayResponse(malformed + valid),
        )
    }

    @Test
    fun emptyResponseHasNoFrames() {
        assertEquals(RelayResponseSummary(), summarizeRelayResponse(byteArrayOf()))
    }
}
