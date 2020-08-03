/*
 * (c) Kitodo. Key to digital objects e. V. <contact@kitodo.org>
 *
 * This file is part of the Kitodo project.
 *
 * It is licensed under GNU General Public License version 3 or later.
 *
 * For the full copyright and license information, please read the
 * GPL3-License.txt file that was distributed with this source code.
 */

package org.kitodo.production.forms.dataeditor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.faces.model.SelectItem;

import org.kitodo.api.dataformat.PhysicalStructure;
import org.kitodo.api.dataformat.View;
import org.kitodo.config.ConfigCore;
import org.kitodo.config.enums.ParameterCore;
import org.kitodo.exceptions.InvalidImagesException;
import org.kitodo.production.helper.Helper;
import org.kitodo.production.helper.metadata.pagination.Paginator;
import org.kitodo.production.helper.metadata.pagination.PaginatorMode;
import org.kitodo.production.helper.metadata.pagination.PaginatorType;
import org.kitodo.production.helper.metadata.pagination.RomanNumeral;
import org.kitodo.production.services.ServiceManager;

/**
 * Backing bean for the pagination panel.
 */
public class PaginationPanel {

    private final DataEditorForm dataEditor;
    private boolean fictitiousCheckboxChecked = false;
    private int newPagesCountValue = 0;
    private List<SelectItem> paginationSelectionItems;
    private List<Integer> paginationSelectionSelectedItems = new ArrayList<>();
    private String paginationStartValue = "1";
    private List<SelectItem> paginationTypeSelectItems;
    private PaginatorType paginationTypeSelectSelectedItem = PaginatorType.ARABIC;
    private List<IllustratedSelectItem> selectPaginationModeItems;
    private IllustratedSelectItem selectPaginationModeSelectedItem;
    private List<SelectItem> selectPaginationScopeItems;
    private Boolean selectPaginationScopeSelectedItem = Boolean.TRUE;

    /**
     * Constructor.
     *
     * @param dataEditor DataEditorForm instance
     */
    PaginationPanel(DataEditorForm dataEditor) {
        this.dataEditor = dataEditor;
        preparePaginationTypeSelectItems();
        prepareSelectPaginationModeItems();
        prepareSelectPaginationScopeItems();
    }

    /**
     * This method is invoked if the create pagination button is clicked.
     */
    public void createPagination() {
        try {
            ServiceManager.getFileService().searchForMedia(dataEditor.getProcess(), dataEditor.getWorkpiece());
        } catch (InvalidImagesException e) {
            Helper.setErrorMessage(e.getLocalizedMessage());
        }
        Paginator paginator = new Paginator(metsEditorDefaultPagination(1));
        List<PhysicalStructure> physicalStructures = dataEditor.getWorkpiece().getAllPhysicalStructuresSorted();
        for (int i = 1; i < physicalStructures.size(); i++) {
            PhysicalStructure physicalStructure = physicalStructures.get(i);
            physicalStructure.setOrder(i);
            physicalStructure.setOrderlabel(paginator.next());
        }
    }

    /**
     * This method is invoked if the generate dummy images button is clicked.
     */
    public void generateDummyImagesButtonClick() {
        List<PhysicalStructure> physicalStructures = dataEditor.getWorkpiece().getAllPhysicalStructuresSorted();
        int order = physicalStructures.isEmpty() ? 1 : physicalStructures.get(physicalStructures.size() - 1).getOrder() + 1;
        boolean withAutomaticPagination = ConfigCore.getBooleanParameter(ParameterCore.WITH_AUTOMATIC_PAGINATION);
        Paginator orderlabel = new Paginator(metsEditorDefaultPagination(order));
        for (int i = 1; i <= newPagesCountValue; i++) {
            PhysicalStructure physicalStructure = new PhysicalStructure();
            physicalStructure.setOrder(order++);
            if (withAutomaticPagination) {
                physicalStructure.setOrderlabel(orderlabel.next());
            }
            physicalStructures.add(physicalStructure);
        }
    }

    private static String metsEditorDefaultPagination(int first) {
        switch (ConfigCore.getParameter(ParameterCore.METS_EDITOR_DEFAULT_PAGINATION)) {
            case "arabic":
                return Integer.toString(first);
            case "roman":
                return RomanNumeral.format(first, true);
            case "uncounted":
                return " - ";
            default:
                return "";
        }
    }

    /**
     * Returns the value of the newPagesCount input box.
     *
     * @return the value of the newPagesCount
     */
    public int getNewPagesCountValue() {
        return newPagesCountValue;
    }

    /**
     * Sets the value of the newPagesCount input box.
     *
     * @param newPagesCountValue
     *            value to set
     */
    public void setNewPagesCountValue(int newPagesCountValue) {
        this.newPagesCountValue = newPagesCountValue;
    }

    /**
     * Returns the selected items of the paginationSelection select menu.
     *
     * @return the selected items of the paginationSelection
     */
    public List<Integer> getPaginationSelectionSelectedItems() {
        return paginationSelectionSelectedItems;
    }

    /**
     * Sets the selected items of the paginationSelection select menu.
     *
     * @param paginationSelectionSelectedItems
     *            selected items to set
     */
    public void setPaginationSelectionSelectedItems(List<Integer> paginationSelectionSelectedItems) {
        this.paginationSelectionSelectedItems = paginationSelectionSelectedItems;
    }

    /**
     * Returns the value of the paginationStart input box.
     *
     * @return the value of the paginationStart
     */
    public String getPaginationStartValue() {
        return paginationStartValue;
    }

    /**
     * Sets the value of the paginationStart input box.
     *
     * @param paginationStartValue
     *            value to set
     */
    public void setPaginationStartValue(String paginationStartValue) {
        this.paginationStartValue = paginationStartValue;
    }

    /**
     * Returns the selected item of the paginationTypeSelect drop-down menu.
     *
     * @return the selected item of the paginationTypeSelect
     */
    public PaginatorType getPaginationTypeSelectSelectedItem() {
        return paginationTypeSelectSelectedItem;
    }

    /**
     * Sets the selected item of the paginationTypeSelect drop-down menu.
     *
     * @param paginationTypeSelectSelectedItem
     *            selected item to set
     */
    public void setPaginationTypeSelectSelectedItem(PaginatorType paginationTypeSelectSelectedItem) {
        this.paginationTypeSelectSelectedItem = paginationTypeSelectSelectedItem;
    }

    /**
     * Returns the selected item of the selectPaginationMode drop-down menu.
     *
     * @return the selected item of the selectPaginationMode
     */
    public IllustratedSelectItem getSelectPaginationModeSelectedItem() {
        return selectPaginationModeSelectedItem;
    }

    /**
     * Sets the selected item of the paginationModeSelect drop-down menu.
     *
     * @param selectPaginationModeSelectedItem
     *            selected item to set
     */
    public void setSelectPaginationModeSelectedItem(IllustratedSelectItem selectPaginationModeSelectedItem) {
        this.selectPaginationModeSelectedItem = selectPaginationModeSelectedItem;
    }

    /**
     * Returns the selected item of the selectPaginationScope drop-down menu.
     *
     * @return the selected item of the selectPaginationScope
     */
    public Boolean getSelectPaginationScopeSelectedItem() {
        return selectPaginationScopeSelectedItem;
    }

    /**
     * Sets the selected item of the paginationScopeSelect drop-down menu.
     *
     * @param selectPaginationScopeSelectedItem
     *            selected item to set
     */
    public void setSelectPaginationScopeSelectedItem(Boolean selectPaginationScopeSelectedItem) {
        this.selectPaginationScopeSelectedItem = selectPaginationScopeSelectedItem;
    }

    /**
     * Returns the items for the paginationSelection select menu.
     *
     * @return the items for the paginationSelection
     */
    public List<SelectItem> getPaginationSelectionItems() {
        return paginationSelectionItems;
    }


    /**
     * Returns the items for the paginationTypeSelect select menu.
     *
     * @return the items for the paginationTypeSelect
     */
    public List<SelectItem> getPaginationTypeSelectItems() {
        return paginationTypeSelectItems;
    }

    /**
     * Returns the items for the paginationMode select menu.
     *
     * @return the items for the paginationMode
     */
    public List<IllustratedSelectItem> getSelectPaginationModeItems() {
        return selectPaginationModeItems;
    }

    /**
     * Returns the items for the paginationScope select menu.
     *
     * @return the items for the paginationScope
     */
    public List<SelectItem> getSelectPaginationScopeItems() {
        return selectPaginationScopeItems;
    }

    /**
     * Returns whether the fictitiousCheckbox is checked.
     *
     * @return whether the fictitiousCheckbox is checked
     */
    public boolean isFictitiousCheckboxChecked() {
        return fictitiousCheckboxChecked;
    }

    /**
     * Sets whether the fictitiousCheckbox is checked.
     *
     * @param fictitiousCheckboxChecked
     *            whether the checkbox is checked
     */
    public void setFictitiousCheckboxChecked(boolean fictitiousCheckboxChecked) {
        this.fictitiousCheckboxChecked = fictitiousCheckboxChecked;
    }

    private void preparePaginationSelectionItems() {
        List<PhysicalStructure> physicalStructures = dataEditor.getWorkpiece().getAllPhysicalStructuresSorted();
        paginationSelectionItems = new ArrayList<>(physicalStructures.size());
        for (int i = 0; i < physicalStructures.size(); i++) {
            PhysicalStructure physicalStructure = physicalStructures.get(i);
            String label = Objects.isNull(physicalStructure.getOrderlabel()) ? Integer.toString(physicalStructure.getOrder())
                    : physicalStructure.getOrder() + " : " + physicalStructure.getOrderlabel();
            paginationSelectionItems.add(new SelectItem(i, label));
        }
    }

    private void preparePaginationSelectionSelectedItem() {
        PhysicalStructure selectedPhysicalStructure = null;
        Optional<PhysicalStructure> optionalSelectedPhysicalStructure = dataEditor.getSelectedPhysicalStructure();
        if (dataEditor.getStructurePanel().isSeparateMedia() && Objects.nonNull(optionalSelectedPhysicalStructure)
                && optionalSelectedPhysicalStructure.isPresent()
                && "page".equals(optionalSelectedPhysicalStructure.get().getType())) {
            selectedPhysicalStructure = optionalSelectedPhysicalStructure.get();
        } else if (Objects.nonNull(dataEditor.getStructurePanel().getSelectedLogicalNode())) {
            StructureTreeNode structureTreeNode = (StructureTreeNode) dataEditor.getStructurePanel().getSelectedLogicalNode().getData();
            if (structureTreeNode.getDataObject() instanceof View) {
                View view = (View) structureTreeNode.getDataObject();
                selectedPhysicalStructure = view.getPhysicalStructure();
            }
        }
        if (Objects.nonNull(selectedPhysicalStructure)) {
            List<PhysicalStructure> physicalStructures = dataEditor.getWorkpiece().getAllPhysicalStructuresSorted();
            for (int i = 0; i < physicalStructures.size(); i++) {
                PhysicalStructure physicalStructure = physicalStructures.get(i);
                if (physicalStructure.equals(selectedPhysicalStructure)) {
                    setPaginationSelectionSelectedItems(Collections.singletonList(i));
                    break;
                }
            }
        }
    }

    private void preparePaginationTypeSelectItems() {
        paginationTypeSelectItems = new ArrayList<>(5);
        paginationTypeSelectItems.add(new SelectItem(PaginatorType.ARABIC, Helper.getTranslation("arabic")));
        paginationTypeSelectItems.add(new SelectItem(PaginatorType.ROMAN, Helper.getTranslation("roman")));
        paginationTypeSelectItems.add(new SelectItem(PaginatorType.UNCOUNTED, Helper.getTranslation("uncounted")));
        paginationTypeSelectItems
                .add(new SelectItem(PaginatorType.FREETEXT, Helper.getTranslation("paginationFreetext")));
        paginationTypeSelectItems
                .add(new SelectItem(PaginatorType.ADVANCED, Helper.getTranslation("paginationAdvanced")));
    }

    private void prepareSelectPaginationModeItems() {
        selectPaginationModeItems = new ArrayList<>(6);
        selectPaginationModeItems.add(new IllustratedSelectItem(PaginatorMode.PAGES, Helper.getTranslation("pageCount"),
                "paginierung_seite.svg"));
        selectPaginationModeItems.add(new IllustratedSelectItem(PaginatorMode.DOUBLE_PAGES,
                Helper.getTranslation("columnCount"), "paginierung_spalte.svg"));
        selectPaginationModeItems.add(new IllustratedSelectItem(PaginatorMode.FOLIATION,
                Helper.getTranslation("blattzaehlung"), "paginierung_blatt.svg"));
        selectPaginationModeItems.add(new IllustratedSelectItem(PaginatorMode.RECTOVERSO_FOLIATION,
                Helper.getTranslation("blattzaehlungrectoverso"), "paginierung_blatt_rectoverso.svg"));
        selectPaginationModeItems.add(new IllustratedSelectItem(PaginatorMode.RECTOVERSO,
                Helper.getTranslation("pageCountRectoVerso"), "paginierung_seite_rectoverso.svg"));
        selectPaginationModeItems.add(new IllustratedSelectItem(PaginatorMode.DOUBLE_PAGES,
                Helper.getTranslation("pageCountDouble"), "paginierung_doppelseite.svg"));
    }

    private void prepareSelectPaginationScopeItems() {
        selectPaginationScopeItems = new ArrayList<>(2);
        selectPaginationScopeItems
                .add(new SelectItem(Boolean.TRUE, Helper.getTranslation("abDerErstenMarkiertenSeite")));
        selectPaginationScopeItems.add(new SelectItem(Boolean.FALSE, Helper.getTranslation("nurDieMarkiertenSeiten")));
    }

    /**
     * This method is invoked if the start pagination action button is clicked.
     */
    public void startPaginationClick() {
        if (paginationSelectionSelectedItems.isEmpty()) {
            Helper.setErrorMessage("fehlerBeimEinlesen", "No pages selected for pagination.");
            return;
        }
        String selectPaginationSeparatorSelectedItem = ConfigCore.getParameter(ParameterCore.PAGE_SEPARATORS)
                .split(",")[0];
        String initializer = paginationTypeSelectSelectedItem.format(selectPaginationModeSelectedItem.getValue(),
                paginationStartValue, fictitiousCheckboxChecked, selectPaginationSeparatorSelectedItem);
        Paginator paginator = new Paginator(initializer);
        List<PhysicalStructure> physicalStructures = dataEditor.getWorkpiece().getAllPhysicalStructuresSorted();
        if (selectPaginationScopeSelectedItem) {
            for (int i = paginationSelectionSelectedItems.get(0); i < physicalStructures.size(); i++) {
                physicalStructures.get(i).setOrderlabel(paginator.next());
            }
        } else {
            for (int i : paginationSelectionSelectedItems) {
                physicalStructures.get(i).setOrderlabel(paginator.next());
            }
        }
        dataEditor.refreshStructurePanel();
    }

    /**
     * Show.
     */
    public void show() {
        paginationSelectionSelectedItems = new ArrayList<>();
        paginationTypeSelectSelectedItem = PaginatorType.ARABIC;
        selectPaginationModeSelectedItem = null;
        paginationStartValue = "1";
        fictitiousCheckboxChecked = false;
        selectPaginationScopeSelectedItem = Boolean.TRUE;
        preparePaginationSelectionItems();
        preparePaginationSelectionSelectedItem();
    }
}
