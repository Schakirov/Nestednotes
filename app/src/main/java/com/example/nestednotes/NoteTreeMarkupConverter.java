package com.example.nestednotes;

import java.util.Stack;

public class NoteTreeMarkupConverter {

    public static String buildMarkupFromTree(NoteTreeHandler.TreeNode root) {
        StringBuilder markupBuilder = new StringBuilder();
        buildMarkupRecursive(root, markupBuilder);
        return markupBuilder.toString();
    }

    private static void buildMarkupRecursive(NoteTreeHandler.TreeNode node, StringBuilder builder) {
        if (!node.isExpandable()) {
            builder.append(node.getValue());
            return;
        }

        //builder.append(node.isExpanded() ? "◆" : "■");

        builder.append("<expandable expanded='").append(node.isExpanded() ? "1" : "0").append("'>");
        for (NoteTreeHandler.TreeNode child : node.getChildren()) {
            buildMarkupRecursive(child, builder);
        }
        builder.append("</expandable>");
    }

    public static NoteTreeHandler buildTreeFromMarkup(String markup) {
        Stack<NoteTreeHandler.TreeNode> nodeStack = new Stack<>();
        NoteTreeHandler.TreeNode root = null; // Root will be determined dynamically

        int i = 0;
        while (i < markup.length()) {
            if (markup.startsWith("<expandable", i)) {
                int startTagEnd = markup.indexOf('>', i);
                String tag = markup.substring(i, startTagEnd + 1);
                boolean isExpanded = tag.contains("expanded='1'");

                NoteTreeHandler.TreeNode newNode = new NoteTreeHandler.TreeNode(isExpanded ? "◆" : "■", true, isExpanded);
                if (root == null) {
                    newNode.setValue("");
                    root = newNode; // Set the first node as the root
                } else {
                    nodeStack.peek().addChild(newNode);
                }
                nodeStack.push(newNode);
                i = startTagEnd + 1;
            } else if (markup.startsWith("</expandable>", i)) {
                nodeStack.pop();
                i += "</expandable>".length();
            } else {
                int nextTag = markup.indexOf('<', i);
                if (nextTag == -1) {
                    nextTag = markup.length();
                }
                String textContent = markup.substring(i, nextTag);
                NoteTreeHandler.TreeNode textNode = new NoteTreeHandler.TreeNode(textContent, false, false);
                if (root == null) {
                    root = textNode; // Set the first node as the root
                } else {
                    nodeStack.peek().addChild(textNode);
                }
                i = nextTag;
            }
        }

        // Handle case where markup is empty
        if (root == null) {
            root = new NoteTreeHandler.TreeNode("", true, true);
            root.addChild(new NoteTreeHandler.TreeNode("", false, false));
        }

        NoteTreeHandler tree = new NoteTreeHandler();
        tree.setRoot(root);
        return tree;
    }
}
