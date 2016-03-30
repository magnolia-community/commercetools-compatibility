/**
 * This file Copyright (c) 2016 Magnolia International
 * Ltd.  (http://www.magnolia-cms.com). All rights reserved.
 *
 *
 * This program and the accompanying materials are made
 * available under the terms of the Magnolia Network Agreement
 * which accompanies this distribution, and is available at
 * http://www.magnolia-cms.com/mna.html
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package info.magnolia.commercetools.integration.app.item;

import java.util.Objects;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

/**
 * Item id used to identify CommerceTools products and categories. Information stored in this id is &lt;projectId&gt;-&lt;type&gt;-&lt;parentId&gt;-&lt;id&gt;.
 */
public class CommerceToolsItemId {

    /**
     * CommerceTools item types.
     */
    public enum ItemType {
        CATEGORY("category"),
        PRODUCT("product");

        private final String type;

        ItemType(final String type) {
            this.type = type;
        }

        @Override
        public String toString() {
            return type;
        }
    }

    private static final String DELIMITER = "--";

    private static final Pattern ITEM_KEY_PATTERN = Pattern.compile("^(.+)" + DELIMITER + "(category|product)" + DELIMITER + "([A-Fa-f0-9]+-){4}([A-Fa-f0-9]+)" + DELIMITER + "([A-Fa-f0-9]+-){4}([A-Fa-f0-9]+)$");

    private ItemType type;

    private String projectId;

    private String id;

    private String parentId;

    public CommerceToolsItemId(final String projectId, final ItemType type, final String id, final String parentId) {
        if (type == null) {
            throw new IllegalArgumentException("Type cannot be null");
        }
        this.projectId = projectId;
        this.type = type;
        this.id = id;
        this.parentId = parentId;
    }

    public ItemType getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    public String getParentId() {
        return parentId;
    }

    public String getProjectId() {
        return projectId;
    }

    /**
     * Create {@link CommerceToolsItemId} from string representation. Be prepared to handle {@link IllegalArgumentException}
     * in case when type enum {@link ItemType} cannot be created or when passed id is null.
     */
    public static CommerceToolsItemId fromString(String itemId) throws IllegalArgumentException, InvalidItemIdException {
        if (itemId == null) {
            throw new IllegalArgumentException("Item id cannot be null.");
        }
        if (!ITEM_KEY_PATTERN.matcher(itemId).matches()) {
            throw new InvalidItemIdException("Key does not match required pattern");
        }
        String[] arr = StringUtils.splitByWholeSeparator(itemId, DELIMITER);
        String projectId = arr[0];
        ItemType type = ItemType.valueOf(arr[1].toUpperCase());
        String parentId = arr[2];
        String id = arr[3];
        return new CommerceToolsItemId(projectId, type, id, parentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(projectId, type, id, parentId);
    }

    @Override
    public boolean equals(final Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof CommerceToolsItemId)) {
            return false;
        }
        CommerceToolsItemId obj = (CommerceToolsItemId) o;
        return StringUtils.equals(obj.getProjectId(), projectId) && obj.getType().equals(type) && StringUtils.equals(obj.getId(), id) && StringUtils.equals(obj.getParentId(), parentId);
    }

    @Override
    public String toString() {
        return projectId + DELIMITER + type + DELIMITER + parentId + DELIMITER + id;
    }
}
