package io.github.ennuil.libzoomer.api;

import org.jetbrains.annotations.Nullable;

import io.github.ennuil.libzoomer.api.transitions.InstantTransitionMode;
import net.minecraft.util.Identifier;

/**
 * The zoom instance is essentially the zoom. It contains all the values and sub-instances required to zoom.
 */
public class ZoomInstance {
	private final Identifier instanceId;
	private boolean zoom;
	private final double defaultZoomDivisor;
	private double zoomDivisor;
	private final TransitionMode transition;
	private final MouseModifier modifier;
	private final ZoomOverlay overlay;

	/**
	 * Initializes a zoom instance. It must be registered by the instance registry before being functional
	 *
	 * @param instanceId the identifier for this zoom instance
	 * @param defaultZoomDivisor the default zoom divisor, it will be this instance's initial zoom divisor value
	 * @param transition the zoom instance's transition mode, if null, {@link InstantTransitionMode} will be used
	 * @param modifier the zoom instance's mouse modifier, if null, no mouse modifier will be applied
	 * @param overlay the zoom instance's zoom overlay, if null, no zoom overlay will be applied
	 */
	public ZoomInstance(Identifier instanceId, float defaultZoomDivisor, TransitionMode transition, @Nullable MouseModifier modifier, @Nullable ZoomOverlay overlay) {
		this.instanceId = instanceId;
		this.zoom = false;
		this.defaultZoomDivisor = defaultZoomDivisor;
		this.zoomDivisor = this.defaultZoomDivisor;
		this.transition = transition == null ? new InstantTransitionMode() : transition;
		this.modifier = modifier;
		this.overlay = overlay;

		ZoomRegistry.registerInstance(this);
	}

	/**
	 * Obtains the identifier of this zoom instance.
	 *
	 * @return this zoom instance's identifier
	 */
	public Identifier getInstanceId() {
		return this.instanceId;
	}

	/**
	 * Gets the zoom instance's zoom state.
	 * This is used to check if this instance's sub-instances should be active or not.
	 *
	 * @return the current zoom state
	 */
	public boolean getZoom() {
		return this.zoom;
	}

	/**
	 * Sets the zoom instance's zoom state.
	 *
	 * @param newZoom The new zoom state.
	 * @return the zoom state with the new value
	 */
	public boolean setZoom(boolean newZoom) {
		return this.zoom = newZoom;
	}

	/**
	 * Gets the zoom instance's current zoom divisor.
	 * Do note that this isn't the same as the transition mode's internal multiplier.
	 *
	 * @return the current zoom divisor
	 */
	public double getZoomDivisor() {
		return this.zoomDivisor;
	}

	/**
	 * Sets the zoom instance's current zoom divisor.
	 *
	 * @param newDivisor the new zoom divisor
	 */
	public void setZoomDivisor(double newDivisor) {
		this.zoomDivisor = newDivisor;
	}

	/**
	 * Sets the instance's zoom divisor to the default zoom divisor.
	 */
	public void resetZoomDivisor() {
		this.zoomDivisor = this.defaultZoomDivisor;
	}

	/**
	 * Gets the instance's transition mode.
	 *
	 * @return the transition mode
	 */
	public TransitionMode getTransitionMode() {
		return this.transition;
	}

	/**
	 * Gets the active state from the instance's transition mode.
	 *
	 * @return the transition mode's active state
	 */
	public boolean isTransitionActive() {
		return this.transition.getActive();
	}

	/**
	 * Gets the instance's mouse modifier.
	 *
	 * @return the mouse modifier
	 */
	@Nullable
	public MouseModifier getMouseModifier() {
		return this.modifier;
	}

	/**
	 * Gets the mouse modifier's active state.
	 *
	 * @return the mouse modifier's active state if the modifier isn't null, or {@code false} otherwise
	 */
	public boolean isModifierActive() {
		return this.modifier != null && this.modifier.getActive();
	}

	/**
	 * Gets the instance's zoom overlay.
	 *
	 * @return the zoom overlay
	 */
	@Nullable
	public ZoomOverlay getZoomOverlay() {
		return this.overlay;
	}

	/**
	 * Gets the zoom overlay's active state.
	 *
	 * @return the zoom overlay's active state if the overlay isn't null, or {@code false} otherwise
	 */
	public boolean isOverlayActive() {
		return this.overlay != null && this.overlay.getActive();
	}
}
