/**
 * This packages contains two examples:<br>
 * a) {@link dream.examples.form.simple simple}: uses a simple graph with a
 * probable glitch. There are to different solutions to this glitch:<br>
 * {@link dream.examples.form.simple.FormServer FormServer} depends on Dream's
 * <i>single_glitch_free</i> consistency to solve the glitch and<br>
 * {@link dream.examples.form.simple.GlitchFreeFormServer GlitchFreeFormServer},
 * which ensures itself that no glitch can occur.<br>
 * <br>
 * b) {@link dream.examples.form.complete_glitchfree complete}: uses a different
 * graph, which requires a locking mechanism.
 *
 * <br>
 * <br>
 * Both examples contain an image of their used dependency graph
 * 
 * @author Tobias Becker
 */
// TODO finish description
package dream.examples.form;