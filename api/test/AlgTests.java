//import com.codeforcommunity.auth.JWT.alg.SHA;
//
//import org.junit.Test;
//
//import static junit.framework.TestCase.assertEquals;
//import static junit.framework.TestCase.assertTrue;
//
//public class AlgTests {
//
//  SHA sha;
//
//  private void init() {
//    try {
//      this.sha = new SHA();
//    } catch (Exception e) {
//
//    }
//  }
//  @Test
//  public void testEncodeSign() {
//    init();
//    String s = "this";
//    assertEquals(sha.encode64(s, false), "dGhpcw");
//    assertEquals(sha.decode64(sha.encode64(s, false)), s);
//    assertEquals(sha.hash("s"), "");
//
//
//  }
//
//
//
//}
