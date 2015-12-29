package javareact.common.types;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Locale;

public class StringProxy extends RemoteVar<String> {
  private final String val = "";

  public StringProxy(String host, String object) {
    super(host, object);
  }

  public StringProxy(String object) {
    super(object);
  }

  public int length() {
    return val.length();
  }

  public boolean isEmpty() {
    return val.isEmpty();
  }

  public char charAt(int index) {
    return val.charAt(index);
  }

  public int codePointAt(int index) {
    return val.codePointAt(index);
  }

  public int codePointBefore(int index) {
    return val.codePointBefore(index);
  }

  public int codePointCount(int beginIndex, int endIndex) {
    return val.codePointCount(beginIndex, endIndex);
  }

  public int offsetByCodePoints(int index, int codePointOffset) {
    return val.offsetByCodePoints(index, codePointOffset);
  }

  public void getChars(int srcBegin, int srcEnd, char[] dst, int dstBegin) {
    val.getChars(srcBegin, srcEnd, dst, dstBegin);
  }

  public byte[] getBytes(String charsetName) throws UnsupportedEncodingException {
    return val.getBytes(charsetName);
  }

  public byte[] getBytes(Charset charset) {
    return val.getBytes(charset);
  }

  public byte[] getBytes() {
    return val.getBytes();
  }

  public boolean contentEquals(StringBuffer sb) {
    return val.contentEquals(sb);
  }

  public boolean contentEquals(CharSequence cs) {
    return val.contentEquals(cs);
  }

  public boolean equalsIgnoreCase(String anotherString) {
    return val.equalsIgnoreCase(anotherString);
  }

  public int compareTo(String anotherString) {
    return val.compareTo(anotherString);
  }

  public int compareToIgnoreCase(String str) {
    return val.compareToIgnoreCase(str);
  }

  public boolean regionMatches(int toffset, String other, int ooffset, int len) {
    return val.regionMatches(toffset, other, ooffset, len);
  }

  public boolean regionMatches(boolean ignoreCase, int toffset, String other, int ooffset, int len) {
    return val.regionMatches(ignoreCase, toffset, other, ooffset, len);
  }

  public boolean startsWith(String prefix, int toffset) {
    return val.startsWith(prefix, toffset);
  }

  public boolean startsWith(String prefix) {
    return val.startsWith(prefix);
  }

  public boolean endsWith(String suffix) {
    return val.endsWith(suffix);
  }

  public int indexOf(int ch) {
    return val.indexOf(ch);
  }

  public int indexOf(int ch, int fromIndex) {
    return val.indexOf(ch, fromIndex);
  }

  public int lastIndexOf(int ch) {
    return val.lastIndexOf(ch);
  }

  public int lastIndexOf(int ch, int fromIndex) {
    return val.lastIndexOf(ch, fromIndex);
  }

  public int indexOf(String str) {
    return val.indexOf(str);
  }

  public int indexOf(String str, int fromIndex) {
    return val.indexOf(str, fromIndex);
  }

  public int lastIndexOf(String str) {
    return val.lastIndexOf(str);
  }

  public int lastIndexOf(String str, int fromIndex) {
    return val.lastIndexOf(str, fromIndex);
  }

  public String substring(int beginIndex) {
    return val.substring(beginIndex);
  }

  public String substring(int beginIndex, int endIndex) {
    return val.substring(beginIndex, endIndex);
  }

  public CharSequence subSequence(int beginIndex, int endIndex) {
    return val.subSequence(beginIndex, endIndex);
  }

  public String concat(String str) {
    return val.concat(str);
  }

  public String replace(char oldChar, char newChar) {
    return val.replace(oldChar, newChar);
  }

  public boolean matches(String regex) {
    return val.matches(regex);
  }

  public boolean contains(CharSequence s) {
    return val.contains(s);
  }

  public String replaceFirst(String regex, String replacement) {
    return val.replaceFirst(regex, replacement);
  }

  public String replaceAll(String regex, String replacement) {
    return val.replaceAll(regex, replacement);
  }

  public String replace(CharSequence target, CharSequence replacement) {
    return val.replace(target, replacement);
  }

  public String[] split(String regex, int limit) {
    return val.split(regex, limit);
  }

  public String[] split(String regex) {
    return val.split(regex);
  }

  public String toLowerCase(Locale locale) {
    return val.toLowerCase(locale);
  }

  public String toLowerCase() {
    return val.toLowerCase();
  }

  public String toUpperCase(Locale locale) {
    return val.toUpperCase(locale);
  }

  public String toUpperCase() {
    return val.toUpperCase();
  }

  public String trim() {
    return val.trim();
  }

  @Override
  public String toString() {
    return val.toString();
  }

  public char[] toCharArray() {
    return val.toCharArray();
  }
}
