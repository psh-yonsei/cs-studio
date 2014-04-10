/**
 * 
 */
package org.csstudio.graphene.opiwidgets;

import org.csstudio.graphene.BubbleGraph2DWidget;
import org.csstudio.graphene.HistogramGraph2DWidget;
import org.eclipse.draw2d.IFigure;

/**
 * @author shroffk
 * 
 */
public class HistogramGraph2DWidgetEditPart extends AbstractGraph2DWidgetEditpart<HistogramGraph2DWidgetFigure, HistogramGraph2DWidgetModel> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.csstudio.opibuilder.editparts.AbstractBaseEditPart#doCreateFigure()
	 */
	@Override
	protected IFigure doCreateFigure() {
		HistogramGraph2DWidgetFigure figure = new HistogramGraph2DWidgetFigure(this);
		configure(figure, getWidgetModel());
		return figure;
	}
	
	@Override
	protected void configure(HistogramGraph2DWidgetFigure figure, HistogramGraph2DWidgetModel model) {
		super.configure(figure, model);
		HistogramGraph2DWidget widget = figure.getSWTWidget();
		if (figure.isRunMode()) {
			widget.setHighlightSelectionValue(model.isHighlightSelectionValue());
		}
	}

	@Override
	protected void registerPropertyChangeHandlers() {
		super.registerPropertyChangeHandlers();
		setPropertyChangeHandler(BubbleGraph2DWidgetModel.PROP_HIGHLIGHT_SELECTION_VALUE, getReconfigureWidgetPropertyChangeHandler());
		setPropertyChangeHandler(BubbleGraph2DWidgetModel.PROP_SELECTION_VALUE_PV, getReconfigureWidgetPropertyChangeHandler());
	}

}
