/*******************************************************************************

"FreePastry" Peer-to-Peer Application Development Substrate

Copyright 2002-2007, Rice University. Copyright 2006-2007, Max Planck Institute 
for Software Systems.  All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are
met:

- Redistributions of source code must retain the above copyright
notice, this list of conditions and the following disclaimer.

- Redistributions in binary form must reproduce the above copyright
notice, this list of conditions and the following disclaimer in the
documentation and/or other materials provided with the distribution.

- Neither the name of Rice  University (RICE), Max Planck Institute for Software 
Systems (MPI-SWS) nor the names of its contributors may be used to endorse or 
promote products derived from this software without specific prior written 
permission.

This software is provided by RICE, MPI-SWS and the contributors on an "as is" 
basis, without any representations or warranties of any kind, express or implied 
including, but not limited to, representations or warranties of 
non-infringement, merchantability or fitness for a particular purpose. In no 
event shall RICE, MPI-SWS or contributors be liable for any direct, indirect, 
incidental, special, exemplary, or consequential damages (including, but not 
limited to, procurement of substitute goods or services; loss of use, data, or 
profits; or business interruption) however caused and on any theory of 
liability, whether in contract, strict liability, or tort (including negligence
or otherwise) arising in any way out of the use of this software, even if 
advised of the possibility of such damage.

*******************************************************************************/ 
package rice.pastry.socket;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.mpisws.p2p.transport.identity.IdentitySerializer;
import org.mpisws.p2p.transport.identity.SerializerListener;
import org.mpisws.p2p.transport.multiaddress.MultiInetSocketAddress;
import org.mpisws.p2p.transport.sourceroute.SourceRoute;

import rice.p2p.commonapi.rawserialization.InputBuffer;
import rice.p2p.commonapi.rawserialization.OutputBuffer;
import rice.pastry.Id;
import rice.pastry.NodeHandleFactoryListener;
import rice.pastry.PastryNode;

public class SPNFIdentitySerializer implements 
    IdentitySerializer<TransportLayerNodeHandle<MultiInetSocketAddress>, 
    MultiInetSocketAddress, SourceRoute<MultiInetSocketAddress>> {
  protected PastryNode pn;

  protected SocketNodeHandleFactory factory;

  public SPNFIdentitySerializer(PastryNode pn, SocketNodeHandleFactory factory) {
    this.pn = pn;
    this.factory = factory;
  }

  @Override
public void serialize(OutputBuffer buf,
      TransportLayerNodeHandle<MultiInetSocketAddress> i)
      throws IOException {
    // SocketNodeHandle handle = (SocketNodeHandle)i;
    // i.getAddress()
    long epoch = i.getEpoch();
    Id nid = (rice.pastry.Id) i.getId();
    // logger.log("serialize("+i+") epoch:"+i.getEpoch()+" nid:"+nid);
    buf.writeLong(epoch);
    nid.serialize(buf);
  }

  /**
   * This is different from the normal deserializer b/c we already have the address
   */
  @Override
public TransportLayerNodeHandle<MultiInetSocketAddress> deserialize(
      InputBuffer buf, SourceRoute<MultiInetSocketAddress> i)
      throws IOException {
    long epoch = buf.readLong();
    Id nid = Id.build(buf);
    
    SocketNodeHandle ret = buildSNH(buf, i.getLastHop(), epoch, nid);
    return factory.coalesce(ret);
  }
  
  protected SocketNodeHandle buildSNH(InputBuffer buf, MultiInetSocketAddress i, long epoch, Id nid) throws IOException {
    return new SocketNodeHandle(i, epoch, nid, pn);
  }

  @Override
public MultiInetSocketAddress translateDown(
      TransportLayerNodeHandle<MultiInetSocketAddress> i) {
    return i.getAddress();
  }

  @Override
public MultiInetSocketAddress translateUp(SourceRoute<MultiInetSocketAddress> i) {
    return i.getLastHop();
  }

  Map<SerializerListener<TransportLayerNodeHandle<MultiInetSocketAddress>>, NodeHandleFactoryListener<SocketNodeHandle>> listeners = 
    new HashMap<SerializerListener<TransportLayerNodeHandle<MultiInetSocketAddress>>, NodeHandleFactoryListener<SocketNodeHandle>>();
  @Override
public void addSerializerListener(final 
      SerializerListener<TransportLayerNodeHandle<MultiInetSocketAddress>> listener) {
    NodeHandleFactoryListener<SocketNodeHandle> foo = new NodeHandleFactoryListener<SocketNodeHandle>() {
      @Override
	public void nodeHandleFound(SocketNodeHandle nodeHandle) {
        listener.nodeHandleFound(nodeHandle);
      }      
    };
    listeners.put(listener, foo);
    factory.addNodeHandleFactoryListener(foo);
  }

  @Override
public void removeSerializerListener(final 
      SerializerListener<TransportLayerNodeHandle<MultiInetSocketAddress>> listener) {
    factory.removeNodeHandleFactoryListener(listeners.get(listener));
  }
}