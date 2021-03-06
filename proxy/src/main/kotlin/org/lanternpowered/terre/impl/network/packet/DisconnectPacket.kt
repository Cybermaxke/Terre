/*
 * Terre
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) contributors
 *
 * This work is licensed under the terms of the MIT License (MIT). For
 * a copy, see 'LICENSE.txt' or <https://opensource.org/licenses/MIT>.
 */
package org.lanternpowered.terre.impl.network.packet

import org.lanternpowered.terre.impl.network.Packet
import org.lanternpowered.terre.impl.network.buffer.readPlainText
import org.lanternpowered.terre.impl.network.buffer.writePlainText
import org.lanternpowered.terre.impl.network.packetDecoderOf
import org.lanternpowered.terre.impl.network.packetEncoderOf
import org.lanternpowered.terre.text.Text

internal data class DisconnectPacket(val reason: Text) : Packet

internal val DisconnectEncoder = packetEncoderOf<DisconnectPacket> { buf, packet -> buf.writePlainText(packet.reason) }

internal val DisconnectDecoder = packetDecoderOf { buf -> DisconnectPacket(buf.readPlainText()) }
