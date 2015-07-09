package javareact.common.types;

import java.util.Locale;

import javareact.common.packets.content.ValueType;

public class ObservableString extends Var<String> {
  public ObservableString(String observableId, boolean persistent, String val) {
    super(observableId, persistent, val);
  }

  public ObservableString(String observableId, String val) {
    super(observableId, val);
  }

  public int length() {
    return super.get().length();
  }

  public boolean isEmpty() {
    return super.get().isEmpty();
  }

  public char charAt(int index) {
    return super.get().charAt(index);
  }

  public int codePointAt(int index) {
    return super.get().codePointAt(index);
  }

  public int codePointBefore(int index) {
    return super.get().codePointBefore(index);
  }

  public int codePointCount(int beginIndex, int endIndex) {
    return super.get().codePointCount(beginIndex, endIndex);
  }

  public int offsetByCodePoints(int index, int codePointOffset) {
    return super.get().offsetByCodePoints(index, codePointOffset);
  }

  public byte[] getBytes() {
    return super.get().getBytes();
  }

  public boolean contentEquals(StringBuffer sb) {
    return super.get().contentEquals(sb);
  }

  public boolean contentEquals(CharSequence cs) {
    return super.get().contentEquals(cs);
  }

  public int compareTo(String anotherString) {
    return super.get().compareTo(anotherString);
  }

  public int compareToIgnoreCase(String str) {
    return super.get().compareToIgnoreCase(str);
  }

  public boolean regionMatches(int toffset, String other, int ooffset, int len) {
    return super.get().regionMatches(toffset, other, ooffset, len);
  }

  public boolean regionMatches(boolean ignoreCase, int toffset, String other, int ooffset, int len) {
    return super.get().regionMatches(ignoreCase, toffset, other, ooffset, len);
  }

  public boolean startsWith(String prefix, int toffset) {
    return super.get().startsWith(prefix, toffset);
  }

  public boolean startsWith(String prefix) {
    return super.get().startsWith(prefix);
  }

  public boolean endsWith(String suffix) {
    return super.get().endsWith(suffix);
  }

  public int indexOf(int ch) {
    return super.get().indexOf(ch);
  }

  public int indexOf(int ch, int fromIndex) {
    return super.get().indexOf(ch, fromIndex);
  }

  public int lastIndexOf(int ch) {
    return super.get().lastIndexOf(ch);
  }

  public int lastIndexOf(int ch, int fromIndex) {
    return super.get().lastIndexOf(ch, fromIndex);
  }

  public int indexOf(String str) {
    return super.get().indexOf(str);
  }

  public int indexOf(String str, int fromIndex) {
    return super.get().indexOf(str, fromIndex);
  }

  public int lastIndexOf(String str) {
    return super.get().lastIndexOf(str);
  }

  public int lastIndexOf(String str, int fromIndex) {
    return super.get().lastIndexOf(str, fromIndex);
  }

  public String substring(int beginIndex) {
    return super.get().substring(beginIndex);
  }

  public String substring(int beginIndex, int endIndex) {
    return super.get().substring(beginIndex, endIndex);
  }

  public CharSequence subSequence(int beginIndex, int endIndex) {
    return super.get().subSequence(beginIndex, endIndex);
  }

  public String concat(String str) {
    return super.get().concat(str);
  }

  public String replace(char oldChar, char newChar) {
    return super.get().replace(oldChar, newChar);
  }

  public boolean matches(String regex) {
    return super.get().matches(regex);
  }

  public boolean contains(CharSequence s) {
    return super.get().contains(s);
  }

  public String replaceFirst(String regex, String replacement) {
    return super.get().replaceFirst(regex, replacement);
  }

  public String replaceAll(String regex, String replacement) {
    return super.get().replaceAll(regex, replacement);
  }

  public String replace(CharSequence target, CharSequence replacement) {
    return super.get().replace(target, replacement);
  }

  public String[] split(String regex, int limit) {
    return super.get().split(regex, limit);
  }

  public String[] split(String regex) {
    return super.get().split(regex);
  }

  public String toLowerCase(Locale locale) {
    return super.get().toLowerCase(locale);
  }

  public String toLowerCase() {
    return super.get().toLowerCase();
  }

  public String toUpperCase(Locale locale) {
    return super.get().toUpperCase(locale);
  }

  public String toUpperCase() {
    return super.get().toUpperCase();
  }

  public String trim() {
    return super.get().trim();
  }

  public char[] toCharArray() {
    return super.get().toCharArray();
  }
  
  @Override
  public final synchronized StringProxy getProxy() {
    return (StringProxy)super.getProxy().toProxyOfType(ValueType.STRING);
  }
}
