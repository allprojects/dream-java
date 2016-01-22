/***
 * * REDS - REconfigurable Dispatching System
 * * Copyright (C) 2003 Politecnico di Milano
 * * <mailto: cugola@elet.polimi.it> <mailto: picco@elet.polimi.it>
 * *
 * * This library is free software; you can redistribute it and/or modify it
 * * under the terms of the GNU Lesser General Public License as published by
 * * the Free Software Foundation; either version 2.1 of the License, or (at
 * * your option) any later version.
 * *
 * * This library is distributed in the hope that it will be useful, but
 * * WITHOUT ANY WARRANTY; without even the implied warranty of
 * * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser
 * * General Public License for more details.
 * *
 * * You should have received a copy of the GNU Lesser General Public License
 * * along with this library; if not, write to the Free Software Foundation,
 * * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA
 ***/

package dream.common;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import protopeer.network.Message;
import protopeer.network.NetworkAddress;

public class Outbox {
  private final Map<Message, Collection<NetworkAddress>> packetsToSend = new HashMap<Message, Collection<NetworkAddress>>();

  // Subject is not used, but introduced to preserve API compatibility
  public void add(String subject, Message packet, Collection<NetworkAddress> recipients) {
    Collection<NetworkAddress> addressSet = packetsToSend.get(packet);
    if (addressSet == null) {
      addressSet = new HashSet<NetworkAddress>();
      packetsToSend.put(packet, addressSet);
    }
    addressSet.addAll(recipients);
  }

  public Set<Message> getPacketsToSend() {
    return packetsToSend.keySet();
  }

  public Collection<NetworkAddress> getRecipientsFor(Message packet) {
    return packetsToSend.get(packet);
  }

}
