/**
 * This packages contains two examples:<br>
 * a) {@link dream.examples.form.simple simple}: uses a simple graph with a
 * probable glitch. There are two different solutions to this glitch:<br>
 * {@link dream.examples.form.simple.FormServer FormServer} depending on Dream's
 * <i>single_glitch_free</i> consistency to solve the glitch and<br>
 * {@link dream.examples.form.simple.GlitchFreeFormServer GlitchFreeFormServer},
 * which ensures itself that no glitch can occur.<br>
 * <br>
 * b) {@link dream.examples.form.complete_glitchfree complete} uses a different
 * graph, which requires a locking mechanism.
 * {@link dream.examples.form.complete_glitchfree.CompleteGlitchFreeFormServer
 * CompleteGlitchFreeFormServer}, which ensures complete_glitch_freedom itself
 * with a own locking mechanism.<br>
 * <br>
 * <br>
 * Both examples contain an image of their used dependency graph
 * 
 * @author Tobias Becker
 */
package dream.examples.form;