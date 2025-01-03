package com.example.nestednotes;

import java.util.ArrayList;
import java.util.List;

public class NoteTreeHandler {

    public static class TreeNode {
        private String value;
        private boolean isExpandable;
        private boolean isExpanded;
        private List<TreeNode> children;

        public TreeNode(String value, boolean isExpandable, boolean isExpanded) {
            this.value = value;
            this.isExpandable = isExpandable;
            this.isExpanded = isExpanded;
            this.children = isExpandable ? new ArrayList<>() : null;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public boolean isExpandable() {
            return isExpandable;
        }

        public boolean isExpanded() {
            return isExpanded;
        }

        public void setExpanded(boolean expanded) {
            this.isExpanded = expanded;
        }

        public List<TreeNode> getChildren() {
            return children;
        }

        public void addChild(TreeNode child) {
            if (isExpandable) {
                children.add(child);
            }
        }

        public void addChildAt(TreeNode child, int position) {
            if (isExpandable && position >= 0 && position <= children.size()) {
                children.add(position, child);
            }
        }
    }

    private TreeNode root;

    public NoteTreeHandler() {
        this.root = new TreeNode("", true, true); // Root node is a placeholder
        TreeNode defaultChild = new TreeNode("", false, false);
        root.addChild(defaultChild);
    }

    public TreeNode getRoot() {
        return root;
    }

    public void addSubstring(int textPosition, String substring) {
        TreeNode targetNode = findNodeByTextPosition(root, textPosition, 0, true);
        if (targetNode != null) {
            int relativePosition = textPosition - getNodeStartTextPosition(root, targetNode, 0);
            String currentValue = targetNode.getValue();
            String newValue = currentValue.substring(0, relativePosition) + substring + currentValue.substring(relativePosition);
            targetNode.setValue(newValue);
        }
    }

    public void addChildAtPosition(int textPosition) {
        TreeNode targetNode = findNodeByTextPosition(root, textPosition, 0, true);
        TreeNode parentNode = findParentNode(root, targetNode);

        if (targetNode != null && parentNode != null) {
            int relativePosition = textPosition - getNodeStartTextPosition(root, targetNode, 0);

            // Split the existing targetNode into two parts
            String originalValue = targetNode.getValue();
            TreeNode beforeSplit = new TreeNode(originalValue.substring(0, relativePosition), false, false);
            TreeNode afterSplit = new TreeNode(originalValue.substring(relativePosition + 1), false, false);

            TreeNode newChild = new TreeNode("■", true, false);

            // Replace the targetNode with new children in the parent's child list
            int targetIndex = parentNode.getChildren().indexOf(targetNode);
            if (targetIndex != -1) {
                parentNode.getChildren().set(targetIndex, beforeSplit);
                parentNode.addChildAt(newChild, targetIndex + 1);
                TreeNode newGrandChild = new TreeNode("\n... \n", false, false);
                newChild.addChildAt(newGrandChild, 0);
                parentNode.addChildAt(afterSplit, targetIndex + 2);
            }
        }
        textPosition = textPosition;
    }

    public void removeSubstring(int textPosition, int length) {
        TreeNode targetNode = findNodeByTextPosition(root, textPosition, 0, false);
        if (targetNode != null) {
            int relativePosition = textPosition - getNodeStartTextPosition(root, targetNode, 0);
            String currentValue = targetNode.getValue();
            String newValue = currentValue.substring(0, relativePosition) + currentValue.substring(relativePosition + length);
            targetNode.setValue(newValue);
        }
    }

    public void minimizeNode(int textPosition) {
        TreeNode targetNode = findNodeByTextPosition(root, textPosition, 0, false);
        if (targetNode != null && targetNode.isExpandable()) {
            targetNode.setExpanded(false);
            targetNode.setValue("■");
        }
    }

    public void expandNode(int textPosition) {
        TreeNode targetNode = findNodeByTextPosition(root, textPosition, 0, false);
        if (targetNode != null && targetNode.isExpandable()) {
            targetNode.setExpanded(true);
            targetNode.setValue("◆");
            textPosition = textPosition;
        }
    }

    public List<String> calculateSymbolsToDeleteForMinimization(int textPosition) {
        TreeNode targetNode = findNodeByTextPosition(root, textPosition, 0, false);
        List<String> symbolsToDelete = new ArrayList<>();
        if (targetNode != null && targetNode.isExpandable()) {
            collectSymbolsForNode(targetNode, symbolsToDelete);
        }
        return symbolsToDelete;
    }

    public List<String> calculateSymbolsToAddForExpansion(int textPosition) {
        TreeNode targetNode = findNodeByTextPosition(root, textPosition, 0, false);
        List<String> symbolsToAdd = new ArrayList<>();
        if (targetNode != null && targetNode.isExpandable()) {
            collectSymbolsForNode(targetNode, symbolsToAdd);
        }
        if (symbolsToAdd.isEmpty()) {
            symbolsToAdd.add("\n...... \n");
        }
        return symbolsToAdd;
    }

    private void collectSymbolsForNode(TreeNode node, List<String> symbols) {
        collectSymbolsForNode(node, symbols, true); // Overloading
    }

    private void collectSymbolsForNode(TreeNode node, List<String> symbols, boolean FirstCall) {
        if (!node.isExpandable()) {
            symbols.add(node.getValue());
        } else {
            symbols.add(node.getValue()); // Add start marker
            if (node.isExpanded() || FirstCall) {
                for (TreeNode child : node.getChildren()) {
                    collectSymbolsForNode(child, symbols, false);
                }
                //symbols.add("□"); // Add end marker
            }
        }
        symbols = symbols;
    }

    public TreeNode findNodeByTextPosition(TreeNode node, int textPosition, int currentPos, boolean left_mode) {
        // left_mode is for adding to the end of the left mode when ambiguous
        if (node != root) {
            // if (!node.isExpandable()), we also need to handle expandable nodes
            int endPos = currentPos + node.getValue().length();
            if (textPosition >= currentPos && textPosition < endPos + (left_mode ? 1 : 0)) {
                return node;
            }
            // return null; it would be BFS here
        }

        if (node.isExpanded()) {
            currentPos += node.getValue().length();
            for (TreeNode child : node.getChildren()) { //getNodeTextLength(child);
                TreeNode found = findNodeByTextPosition(child, textPosition, currentPos, left_mode);
                if (found != null) {
                    return found;
                }
                currentPos += getNodeTextLength(child);
            }
        }

        return null;
    }

    private TreeNode findParentNode(TreeNode parent, TreeNode target) {
        List<TreeNode> children = parent.getChildren();
        if (children == null) {
            return null;
        }

        if (children.contains(target)) {
            return parent;
        }

        for (TreeNode child : parent.getChildren()) {
            TreeNode found = findParentNode(child, target);
            if (found != null) {
                return found;
            }
        }

        return null;
    }

    private int getNodeStartTextPosition(TreeNode root, TreeNode targetNode, int currentPos) {
        if (root == targetNode) {
            return currentPos;
        }

        if (root.isExpandable() && root.isExpanded()) {
            currentPos += root.getValue().length();
            for (TreeNode child : root.getChildren()) { //getNodeTextLength(root);
                int childStartPos = getNodeStartTextPosition(child, targetNode, currentPos);
                if (childStartPos != -1) {
                    return childStartPos;
                }
                /*if (!(child.isExpandable() && child.isExpanded())) {
                    // don't add if it had been already added as a root previously
                    currentPos += child.getValue().length();
                }*/
                currentPos += getNodeTextLength(child);
            }
        }

        return -1;
    }

    private int getNodeTextLength(TreeNode node) {
        if (!node.isExpandable()) {
            return node.getValue().length();
        }

        if (!node.isExpanded()) {
            return 1; // Represented by "■" in text mode
        }

        int length = node.getValue().length(); // "■" at the start, or "" for root
        for (TreeNode child : node.getChildren()) {
            length += getNodeTextLength(child);
        }
        //length += 1; // "□" at the end

        return length;
    }
}
