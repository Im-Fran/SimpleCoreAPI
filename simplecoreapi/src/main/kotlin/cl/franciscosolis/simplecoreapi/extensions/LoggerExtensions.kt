/*
 * SimpleCoreAPI - Kotlin Project Library
 * Copyright (C) 2024 Francisco Solís
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package cl.franciscosolis.simplecoreapi.extensions

import java.util.logging.Logger

/**
 * Log a 'FINEST' message.
 *
 * If the logger is currently enabled for the 'FINEST' message
 * level then the given message is forwarded to all the
 * registered output Handler objects.
 *
 * @param msg The string message (or a key in the message catalog)
 */
fun Logger.debug(msg: String) = this.finest(msg)