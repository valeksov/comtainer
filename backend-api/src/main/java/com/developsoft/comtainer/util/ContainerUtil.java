package com.developsoft.comtainer.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import com.developsoft.comtainer.rest.dto.CargoItemPlacementDto;
import com.developsoft.comtainer.rest.dto.ContainerDto;
import com.developsoft.comtainer.rest.dto.LoadPlanStepDto;
import com.developsoft.comtainer.runtime.model.BasePackage;
import com.developsoft.comtainer.runtime.model.CargoItemStepRuntime;
import com.developsoft.comtainer.runtime.model.ContainerArea;
import com.developsoft.comtainer.runtime.model.MatrixElement;
import com.developsoft.comtainer.runtime.model.MatrixRow;
import com.developsoft.comtainer.util.comparators.LoadPlanStepWeightComparator;

public class ContainerUtil {

	public static boolean placeRuntimeStep (final List<MatrixRow> matrix, final CargoItemStepRuntime step) {
		final MatrixElement el = findFreeElement(matrix, step);
		if (el != null) {
			step.updateCoordinates(el.getRow().getRowNum(), el.getColNum(), 0);
			markedPlaced(matrix, el.getRow().getRowNum(), el.getColNum(), step.getLength(), step.getWidth());
			return true;
		}
		return false;
	}

	public static boolean placeRuntimeStep (final List<MatrixRow> matrix, final BasePackage step) {
		System.out.print("Placing ("+step.getHorizontalRotation());
		final MatrixElement el = findFreeElement(matrix, step);
		if (el != null && el.getRow().getRowNum()+step.getLength() < matrix.size() && el.getColNum()+step.getWidth() < el.getRow().getNumColumns()) {
			System.out.println(","+step.getHorizontalRotation()+") "+step.getLength()+" x "+step.getWidth() + " on ["+el.getRow().getRowNum()+","+el.getColNum()+"]");
			step.setPlacedInContainer(true);
			step.updateContainerCoordinates(el.getRow().getRowNum(), el.getColNum());
			markedPlaced(matrix, el.getRow().getRowNum(), el.getColNum(), step.getLength(), step.getWidth());
			return true;
		}
		return false;
	}
	
	public static MatrixElement findFreeElement (final List<MatrixRow> matrix, final BasePackage step) {
		MatrixElement el = findFreeElement(matrix, step.getLength(), step.getWidth());
		if (el == null) {
			el = findFreeElement(matrix, step.getWidth(), step.getLength());
			if (el != null) {
				step.rotate(1);
			}
		}
		return el;
	}
	
	public static boolean build (final List<LoadPlanStepDto> loadPlanSteps, final ContainerDto container) {
		List<LoadPlanStepDto> failedSteps = buildOnce(loadPlanSteps, container);
		while (failedSteps.size() > 0 && splitFailedSteps(loadPlanSteps, failedSteps)) {
			loadPlanSteps.forEach(step -> resetCoordinates(step));
			Collections.sort(loadPlanSteps, new LoadPlanStepWeightComparator());			
			failedSteps = buildOnce(loadPlanSteps, container);
		}
		return failedSteps.size() == 0;
	}
	
	private static boolean splitFailedSteps (final List<LoadPlanStepDto> loadPlanSteps, final List<LoadPlanStepDto> failedSteps) {
		boolean result = false;
		for (final LoadPlanStepDto nextFailed : failedSteps) {
			final int itemSize = nextFailed.getItems().size();
			if (itemSize > 1) {
				final int middle = (itemSize + 1) / 2;
				final LoadPlanStepDto newStep = new LoadPlanStepDto();
				newStep.setId(UUID.randomUUID().toString());
				newStep.setStartX(0);
				newStep.setStartY(0);
				newStep.setStartZ(0);
				newStep.setWidth(nextFailed.getWidth());
				newStep.setHeight(nextFailed.getHeight());
				final List<CargoItemPlacementDto> firstItems = new ArrayList<CargoItemPlacementDto>();
				final List<CargoItemPlacementDto> secondItems = new ArrayList<CargoItemPlacementDto>();
				
				int itemNewStartX = 0;
				for (int i = 0; i < itemSize; i++) {
					final CargoItemPlacementDto nextDto =  nextFailed.getItems().get(i);
					if (i < middle) {
						firstItems.add(nextDto);
					} else {
						nextDto.setStartX(itemNewStartX);
						itemNewStartX += getItemLength(nextDto);
						secondItems.add(nextDto);
					}
				}
				nextFailed.setItems(firstItems);
				nextFailed.setLength(calculateLength(firstItems));
				newStep.setItems(secondItems);
				newStep.setLength(calculateLength(secondItems));
				loadPlanSteps.add(newStep);
			}
		}
		return result;
	}
	
	private static int getItemLength(final CargoItemPlacementDto item) {
		int length = 0;
		switch (item.getOrientation()) {
			case 2: length = item.getCargo().getWidth(); break;
			case 4: length = item.getCargo().getHeight(); break;
			case 5: length = item.getCargo().getWidth(); break;
			case 6: length = item.getCargo().getHeight(); break;
			default : length = item.getCargo().getLength();
		}
		return length;
	}
	private static int calculateLength(final List<CargoItemPlacementDto> items) {
		int result = 0;
		for (final CargoItemPlacementDto next : items) {
			result += getItemLength(next);
		}
		return result;
	}
	private static List<LoadPlanStepDto> buildOnce (final List<LoadPlanStepDto> loadPlanSteps, final ContainerDto container) {
		final List<LoadPlanStepDto> failedSteps = new ArrayList<LoadPlanStepDto>();
		final List<ContainerArea> areaPlan = init(container);
		for (final LoadPlanStepDto nextStep : loadPlanSteps) {
			if (!addNewStep(areaPlan, nextStep, null, container)) {
				failedSteps.add(nextStep);
			}
		}
		return failedSteps;
	}
	
	public static List<ContainerArea> init(final ContainerDto container) {
		final List<ContainerArea> result = new ArrayList<ContainerArea>();
		result.add(createFreeArea(0, 0, 0, container.getLength(), container.getWidth(), container.getHeight()));
		return result;
	}

	public static List<MatrixRow> init2DMatrix(final ContainerDto container) {
		final List<MatrixRow> result = new ArrayList<MatrixRow>();
		for (int rowNum = 0; rowNum < container.getLength(); rowNum++) {
			result.add(new MatrixRow(rowNum, container.getWidth()));
		}
		return result;
	}
	
	public static MatrixElement findFreeElement (final List<MatrixRow> matrix, final CargoItemStepRuntime step) {
		return findFreeElement(matrix, step.getLength(), step.getWidth());
	}
	public static MatrixElement findFreeElement (final List<MatrixRow> matrix, final int len, final int width) {
		for (int i = 0; i < matrix.size() - len; i++) {
			final MatrixRow row = matrix.get(i);
			if (row.getNumberFreeElements() >= width && row.getNumberFreeElements() >= getMaxFreeChain(row)) {
				for (int j = 0; j < row.getNumColumns() - width; j++) {
					final MatrixElement curEl = row.getElements().get(j);
					if (isFree(matrix, curEl, len, width)) {
						return curEl;
					}
				}
			}
		}
		return null;
	}
	
	private static int getMaxFreeChain (final MatrixRow row) {
		int maxChain = 0;
		int curChain = 0;
		for (int i = 0; i < row.getNumColumns(); i++) {
			final MatrixElement curEl = row.getElements().get(i);
			if (!curEl.isPlaced()) {
				curChain++;
			} else {
				if (curChain > maxChain) {
					maxChain = curChain;
				}
				curChain = 0;
			}
		}
		return maxChain;
	}
	public static void markedPlaced (final List<MatrixRow> matrix, final int startRow, final int startCol, final int numRows, final int numColumns) {
		for (int i = startRow; i < startRow + numRows; i++) {
			final MatrixRow row = matrix.get(i);
			for (int j= startCol; j < startCol + numColumns; j++) {
				final MatrixElement curEl = row.getElements().get(j);
				curEl.place();
			}
		}
	}
	
	private static boolean isFree (final List<MatrixRow> matrix, final MatrixElement startEl, final int numRows, final int numColumns) {
		if (startEl.getRow().getRowNum() + numRows > matrix.size()) {
			return false;
		}
		if (startEl.getColNum() + numColumns > matrix.get(0).getNumColumns()) {
			return false;
		}
		for (int i = startEl.getRow().getRowNum(); i < startEl.getRow().getRowNum() + numRows; i++) {
			final MatrixRow row = matrix.get(i);
			if (row.getNumberFreeElements() < numColumns || row.getNumberFreeElements() < getMaxFreeChain(row)) {
				return false;
			}
			for (int j= startEl.getColNum(); j < startEl.getColNum() + numColumns; j++) {
				if (row.getElements().get(j).isPlaced()) {
					return false;
				}
			}
		}
		return true;
	}
	
	private static boolean withinBoundary (final int coord, final int start, final int size) {
		return (coord >= start) && (coord < (start + size));
	}
	
	private static ContainerArea createFreeArea (final int startX, final int startY, final int startZ, final int length, final int width, final int height) {
		final ContainerArea result = new ContainerArea();
		result.setFree(true);
		result.setStartX(startX);
		result.setStartY(startY);
		result.setStartZ(startZ);
		result.setLength(length);
		result.setWidth(width);
		result.setHeight(height);
		return result;
	}
	
	public static List<ContainerArea> buildfromRequest(final ContainerDto container) {
		final List<ContainerArea> areaPlan = init(container);
		if (container.getLoadPlan() != null && container.getLoadPlan().getLoadPlanSteps() != null) {
			container.getLoadPlan().getLoadPlanSteps().forEach(step -> addExistingStep(areaPlan, step));
		}
		Collections.sort(areaPlan);
		return areaPlan;
	}
	
	public static void addExistingStep (final List<ContainerArea> areaPlan, final LoadPlanStepDto step) {
		final ContainerArea freeArea = findArea(areaPlan, step);
		if (freeArea != null) {
			placeStep(areaPlan, step, freeArea);
		}
	}
	
	public static boolean addNewStep (final List<ContainerArea> areaPlan, final CargoItemStepRuntime step, final ContainerDto container) {
		final ContainerArea freeArea = findArea(areaPlan, step);
		if (freeArea != null) {
			step.updateCoordinates(freeArea.getStartX(), freeArea.getStartY(), freeArea.getStartZ());
			placeStep(areaPlan, step, freeArea);
			Collections.sort(areaPlan);
			return true;
		}		
		return false;
	}
	
	public static boolean addNewStep (final List<ContainerArea> areaPlan, final LoadPlanStepDto step, final Integer cargoSupport, final ContainerDto container) {
		final ContainerArea freeArea = findArea(areaPlan, step, cargoSupport, container);
		if (freeArea != null) {
			updateCoordinates(freeArea, step);
			placeStep(areaPlan, step, freeArea);
			//TODO correct free areas to take in account if cargo is bigger than the working area
			Collections.sort(areaPlan);
			return true;
		}
		return false;
	}
	
	private static void resetCoordinates(final LoadPlanStepDto step) {
		if (step.getItems() != null) {
			for (final CargoItemPlacementDto nextDto : step.getItems()) {
				nextDto.setStartX(nextDto.getStartX() - step.getStartX());
				nextDto.setStartY(nextDto.getStartY() - step.getStartY());
				nextDto.setStartZ(nextDto.getStartZ() - step.getStartZ());
			}
		}
		step.setStartX(0);
		step.setStartY(0);
		step.setStartZ(0);
	}
	
	private static void updateCoordinates(final ContainerArea freeArea, final LoadPlanStepDto step) {
		step.setStartX(freeArea.getStartX());
		step.setStartY(freeArea.getStartY());
		step.setStartZ(freeArea.getStartZ());
		if (step.getItems() != null) {
			for (final CargoItemPlacementDto nextDto : step.getItems()) {
				nextDto.setStartX(nextDto.getStartX() + step.getStartX());
				nextDto.setStartY(nextDto.getStartY() + step.getStartY());
				nextDto.setStartZ(nextDto.getStartZ() + step.getStartZ());
			}
		}
	}
	
	
	//Currently there is not logic to deal with not stackable items
	
	/*
	 * There will be two steps to check if cargo will fit in an area. 
	 * First will be without cargo support = null. Thi smeans that we will try to find area which will fully support the cargo
	 * If no such is available we will try again with the cargo support on. In the second step we will ignore all floor areas, 
	 * since it become meaningless to put bigger item on the floor: it will simply not fit in the container at all 
	*/
	private static ContainerArea findArea(final List<ContainerArea> areaPlan, final CargoItemStepRuntime step) {
		for (final ContainerArea nextArea : areaPlan) {
			if (nextArea.isFree() && nextArea.getLength() >= step.getLength() && nextArea.getWidth() >= step.getWidth()) {
				return nextArea;
			}
		}
		return null;
	}
	
	private static ContainerArea findArea(final List<ContainerArea> areaPlan, final LoadPlanStepDto step, final Integer cargoSupport, final ContainerDto container) {
		for (final ContainerArea nextArea : areaPlan) {
			if (nextArea.isFree() && nextArea.getHeight() >= step.getHeight()) {
				//TODO Check Weigth on bottom (if any) against current step
				if (nextArea.getLength() >= step.getLength() && nextArea.getWidth() >= step.getWidth()) {
					return nextArea;
				}
				
				//also add a check for the next area in case cargoSupport in not null & workingValue has been corrected
				//Also take in account if in this case the new cargo item will fit in container at all: startX/Y + biggerDimension <= container.length/width
				
			}
		}
		return null;
	}
	
	private static ContainerArea findArea(final List<ContainerArea> areaPlan, final LoadPlanStepDto step) {
		for (final ContainerArea nextArea : areaPlan) {
			if (nextArea.isFree()) {
				if (withinBoundary(step.getStartX(), nextArea.getStartX(), nextArea.getLength()) &&
						withinBoundary(step.getStartY(), nextArea.getStartY(), nextArea.getWidth()) &&
						step.getStartZ().equals(nextArea.getStartZ())/*We can't place cargo on the air*/) {
					return nextArea;
				}
			}
		}
		return null;
	}

	private static void placeStep (final List<ContainerArea> areaPlan, final CargoItemStepRuntime step, final ContainerArea freeArea) {
		final int oldAreaLength = freeArea.getLength();
		final int oldAreaWidth = freeArea.getWidth();
		final int oldAreaHeight = freeArea.getHeight();
		final int oldAreaStartX = freeArea.getStartX();
		final int oldAreaStartY = freeArea.getStartY();
		final int oldAreaStartZ = freeArea.getStartZ();

		//1. the original area will become the one with placed cargo
		freeArea.setLength(step.getLength());
		freeArea.setWidth(step.getWidth());
		freeArea.setHeight(step.getHeight());
		freeArea.setFree(false);

		if (step.getLength() < oldAreaLength) {
			final int afterXStartX = oldAreaStartX +  step.getLength();
			final int afterXLength = oldAreaLength - step.getLength();
			final ContainerArea afterX = createFreeArea(afterXStartX, oldAreaStartY, oldAreaStartZ, afterXLength, step.getWidth(), oldAreaHeight);
			areaPlan.add(afterX);
		}

		if (step.getWidth() < oldAreaWidth) {
			final int afterYStartY = oldAreaStartY +  step.getWidth();
			final int afterYWidth = oldAreaWidth - step.getWidth();
			final ContainerArea oldArea = findFreeAreaBeforeByCoordinates(areaPlan, step.getStartX(), afterYStartY, oldAreaStartZ, afterYWidth);
			if (oldArea != null) {
				oldArea.setLength(oldArea.getLength() + oldAreaLength);
			} else {
				final ContainerArea afterY = createFreeArea(step.getStartX(), afterYStartY, oldAreaStartZ, oldAreaLength, afterYWidth, oldAreaHeight);
				areaPlan.add(afterY);
			}
		}
	}
	
	private static void placeStep (final List<ContainerArea> areaPlan, final LoadPlanStepDto step, final ContainerArea freeArea) {
		final int oldAreaLength = freeArea.getLength();
		final int oldAreaWidth = freeArea.getWidth();
		final int oldAreaHeight = freeArea.getHeight();
		final int oldAreaStartX = freeArea.getStartX();
		final int oldAreaStartY = freeArea.getStartY();
		final int oldAreaStartZ = freeArea.getStartZ();
	
		//1. the original area will become the one with placed cargo
		freeArea.setLength(step.getLength());
		freeArea.setWidth(step.getWidth());
		freeArea.setHeight(step.getHeight());
		freeArea.setStartX(step.getStartX());
		freeArea.setStartY(step.getStartY());
		freeArea.setFree(false);
	
		// 2. We create one free area on top of the area with placed cargo (if there is height remaining)
		if (step.getHeight() < oldAreaHeight) {
			final int aboveStartZ = step.getStartZ() + step.getHeight();
			final int aboveHeight = oldAreaHeight - step.getHeight();
			final ContainerArea above = createFreeArea(step.getStartX(), step.getStartY(), aboveStartZ, step.getLength(), step.getWidth(), aboveHeight);
			areaPlan.add(above);
		}
	
		// 3. We check if startX of step is after the old area startX
		final int beforeXLength = step.getStartX() - oldAreaStartX;
		if (beforeXLength > 0) {
			final ContainerArea beforeX = createFreeArea(oldAreaStartX, oldAreaStartY, oldAreaStartZ, beforeXLength, oldAreaWidth, oldAreaHeight);
			areaPlan.add(beforeX);
		}
	
		// 4. We check if startY step of length is after the old area startY
		final int beforeYWidth = step.getStartY() - oldAreaStartY;
		if (beforeYWidth > 0) {
			final ContainerArea beforeY = createFreeArea(step.getStartX(), oldAreaStartY, oldAreaStartZ, step.getLength(), beforeYWidth, oldAreaHeight);
			areaPlan.add(beforeY);
		}
	
		// 5. We add one free area (if any available) with the remaining width (same length coordinates: startX and length)
		if (beforeYWidth + step.getWidth() < oldAreaWidth) {
			final int afterYStartY = oldAreaStartY + beforeYWidth + step.getWidth();
			final int afterYWidth = oldAreaWidth - (beforeYWidth + step.getWidth());
			final ContainerArea oldArea = findFreeAreaBeforeByCoordinates(areaPlan, step.getStartX(), afterYStartY, oldAreaStartZ, afterYWidth);
			if (oldArea != null) {
				oldArea.setLength(oldArea.getLength()+step.getLength());
			} else {
				final ContainerArea afterY = createFreeArea(step.getStartX(), afterYStartY, oldAreaStartZ, step.getLength(), afterYWidth, oldAreaHeight);
				areaPlan.add(afterY);
			}
		}
		
		// 6. We add one free area (if any available) with the remaining length (same width coordinates: startY and width)
		if (beforeXLength + step.getLength() < oldAreaLength) {
			final int afterXStartX = oldAreaStartX + beforeXLength + step.getLength();
			final int afterXLength = oldAreaLength - (beforeXLength + step.getLength());
			final ContainerArea afterX = createFreeArea(afterXStartX, oldAreaStartY, oldAreaStartZ, afterXLength, oldAreaWidth, oldAreaHeight);
			areaPlan.add(afterX);
		}
	}
	private static ContainerArea findFreeAreaBeforeByCoordinates (final List<ContainerArea> areaPlan, final int startX, final int startY, final int startZ, final int width) {
		for (final ContainerArea nextArea : areaPlan) {
			if (nextArea.isFree() && nextArea.getStartZ() == startZ && nextArea.getStartY() == startY && nextArea.getStartX() < startX && nextArea.getWidth() == width) {
				return nextArea;
			}
		}
		return null;
	}
}
