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
package rice.p2p.glacier;
import java.util.*;
import rice.p2p.commonapi.*;
import rice.p2p.util.*;

/**
 * DESCRIBE THE CLASS
 *
 * @version $Id: FragmentKeySet.java 4654 2009-01-08 16:33:07Z jeffh $
 * @author ahae
 */
@SuppressWarnings("unchecked")
public class FragmentKeySet implements rice.p2p.commonapi.IdSet {

  private SortedMap idSet;

  // a cache of the fingerprint hash
  private FragmentKey cachedHash;
  private boolean validHash;

  /**
   * Constructor.
   */
  public FragmentKeySet() {
    idSet = new RedBlackMap();
    validHash = false;
  }

  /**
   * Copy constructor. constructs a shallow copy of the given IdSet o.
   *
   * @param o the IdSet to copy
   */
  public FragmentKeySet(FragmentKeySet o) {
    idSet = new RedBlackMap(o.idSet);
    cachedHash = o.cachedHash;
    validHash = o.validHash;
  }

  /**
   * Constructor. constructs a shallow copy of the given TreeSet s.
   *
   * @param s the TreeSet based on which we construct a new IdSet
   */
  protected FragmentKeySet(SortedMap s) {
    idSet = s;
    validHash = false;
  }

  /**
   * test membership
   *
   * @param id the id to test
   * @return true of id is a member, false otherwise
   */
  public boolean isMember(rice.p2p.commonapi.Id id) {
    return idSet.containsKey(id);
  }

  /**
   * return an iterator over the elements of this set
   *
   * @return the interator
   */
  @Override
public Iterator<Id> getIterator() {
    return idSet.keySet().iterator();
  }

  /**
   * compute a fingerprint of the members in this IdSet
   *
   * @return an Id containing the secure hash of this set
   */

  public byte[] getHash() {
    throw new RuntimeException("FragmentKeySet.getHash() is not supported!");
    /*
     *  if (validHash) return cachedHash;
     *  / recompute the hash
     *  MessageDigest md = null;
     *  try {
     *  md = MessageDigest.getInstance("SHA");
     *  } catch ( NoSuchAlgorithmException e ) {
     *  System.errr.println( "No SHA support!" );
     *  return null;
     *  }
     *  Iterator it = idSet.iterator();
     *  byte[] raw = new byte[Id.IdBitLength / 8];
     *  Id id;
     *  while (it.hasNext()) {
     *  id = (Id) it.next();
     *  id.blit(raw);
     *  md.update(raw);
     *  }
     *  byte[] digest = md.digest();
     *  cachedHash = new Id(digest);
     *  validHash = true;
     *  return cachedHash;
     */
  }

  /**
   * test membership
   *
   * @param id the id to test
   * @return true of id is a member, false otherwise
   */
  @Override
public boolean isMemberId(rice.p2p.commonapi.Id id) {
    return isMember(id);
  }

  /**
   * return the number of elements
   *
   * @return DESCRIBE THE RETURN VALUE
   */
  @Override
public int numElements() {
    return idSet.size();
  }

  /**
   * add a member
   *
   * @param id the id to add
   */
  public void addMember(rice.p2p.commonapi.Id id) {
    idSet.put(id, null);
    validHash = false;
  }

  /**
   * remove a member
   *
   * @param id the id to remove
   */
  public void removeMember(Id id) {
    idSet.remove(id);
    validHash = false;
  }

  /**
   * return the smallest member id
   *
   * @return the smallest id in the set
   */
  public FragmentKey minMember() {
    return (FragmentKey) idSet.firstKey();
  }

  /**
   * return the largest member id
   *
   * @return the largest id in the set
   */
  public FragmentKey maxMember() {
    return (FragmentKey) idSet.lastKey();
  }

  /**
   * return a subset of this set, consisting of the member ids in a given range
   *
   * @param from the counterclockwise end of the range (inclusive)
   * @param to the clockwise end of the range (exclusive)
   * @return the subset
   */
  public FragmentKeySet subSet(Id from, Id to) {
    return new FragmentKeySet(idSet.subMap(from, to));
  }

  /**
   * return a subset of this set, consisting of the member ids in a given range
   *
   * @param range the range
   * @return the subset
   */
  public FragmentKeySet subSet(FragmentKeyRange range) {
    if(range.isEmpty())
      return new FragmentKeySet();
    if(range.getCCWId().equals(range.getCWId()))
      return this;
    else
      return subSet(range.getCCWId(), range.getCWId());
  }


  /**
   * Returns a string representation of the IdSet.
   *
   * @return DESCRIBE THE RETURN VALUE
   */

  @Override
public String toString() {
    Iterator it = getIterator();
    Id key;
    String s = "[ IdSet: ";
    while (it.hasNext()) {
      key = (Id) it.next();
      s = s + key + ",";

    }
    s = s + " ]";
    return s;
  }

  // Common API Support

  /**
   * add a member
   *
   * @param id the id to add
   */
  @Override
public void addId(rice.p2p.commonapi.Id id) {
    addMember(id);
  }

  /**
   * remove a member
   *
   * @param id the id to remove
   */
  @Override
public void removeId(rice.p2p.commonapi.Id id) {
    removeMember(id);
  }

  /**
   * return a subset of this set, consisting of the member ids in a given range
   *
   * @param range DESCRIBE THE PARAMETER
   * @return the subset
   */
  @Override
public rice.p2p.commonapi.IdSet subSet(rice.p2p.commonapi.IdRange range) {
    return subSet((FragmentKeyRange) range);
  }

  /**
   * return a hash of this set
   *
   * @return the hash of this set
   */
  @Override
public byte[] hash() {
    return getHash();
  }

  /**
   * DESCRIBE THE METHOD
   *
   * @return DESCRIBE THE RETURN VALUE
   */
  @Override
public Object clone() {
    return new FragmentKeySet(this);
  }
  
  /**
   * Returns a new, empty IdSet of this type
   *
   * @return A new IdSet
   */
  @Override
public IdSet build() {
    return new FragmentKeySet();
  }

  /**
   * return this set as an array
   * @return the array
   */
  @Override
public rice.p2p.commonapi.Id[] asArray() {
    return (rice.p2p.commonapi.Id[]) idSet.keySet().toArray(new rice.p2p.commonapi.Id[0]);
  }
  
}
