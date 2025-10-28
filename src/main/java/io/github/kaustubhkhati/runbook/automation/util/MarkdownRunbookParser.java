package io.github.kaustubhkhati.runbook.automation.util;

import io.github.kaustubhkhati.runbook.automation.model.Runbook;
import java.util.ArrayList;
import java.util.List;
import org.commonmark.node.BulletList;
import org.commonmark.node.FencedCodeBlock;
import org.commonmark.node.Heading;
import org.commonmark.node.ListItem;
import org.commonmark.node.Node;
import org.commonmark.node.OrderedList;
import org.commonmark.node.Paragraph;
import org.commonmark.node.Text;
import org.commonmark.parser.Parser;
import org.jetbrains.annotations.NotNull;

/**
 * Utility to parse a structured Markdown runbook into a {@link Runbook} object.
 * <p>
 * The Markdown must follow a predictable format using headings:
 * Runbook ID, Title, Description, Tags, Required Variables, Execution Steps.
 * </p>
 */
public class MarkdownRunbookParser {

   /**
    * Parses raw Markdown text to a Runbook instance.
    *
    * @param markdown Raw Markdown runbook text.
    * @return Populated Runbook object.
    */
   public static Runbook parse(String markdown) {
      Parser parser = Parser.builder().build();
      Node document = parser.parse(markdown);

      String id = "";
      String title = "";
      String description = "";
      List<String> tags = new ArrayList<>();
      List<String> requiredVars = new ArrayList<>();
      String executionSteps = "";

      Node currentHeading = null;
      for (Node node = document.getFirstChild(); node != null; node = node.getNext()) {

         // Capture heading node content
         if (node instanceof Heading heading && heading.getLevel() == 2) {
            currentHeading = heading.getFirstChild() != null
                ? heading.getFirstChild()
                : null;
         }
         // Process only if current heading node is a Text node
         else if (currentHeading instanceof Text headingText) {
            switch (headingText.getLiteral()) {
               case "Runbook ID" -> {
                  if (node instanceof Paragraph p && p.getFirstChild() instanceof Text text) {
                     id = text.getLiteral();
                  }
               }
               case "Title" -> {
                  if (node instanceof Paragraph p && p.getFirstChild() instanceof Text text) {
                     title = text.getLiteral();
                  }
               }
               case "Description" -> {
                  if (node instanceof Paragraph p && p.getFirstChild() instanceof Text text) {
                     description = text.getLiteral();
                  }
               }
               case "Tags" -> {
                  if (node instanceof BulletList bl) {
                     for (Node li = bl.getFirstChild(); li != null; li = li.getNext()) {
                        tags.add(((Text) li.getFirstChild().getFirstChild()).getLiteral());
                     }
                  }
               }
               case "Required Variables" -> {
                  if (node instanceof BulletList bl) {
                     for (Node li = bl.getFirstChild(); li != null; li = li.getNext()) {
                        requiredVars.add(((Text) li.getFirstChild().getFirstChild()).getLiteral());
                     }
                  }
               }
               case "Execution Steps" -> {
                  if (node instanceof FencedCodeBlock fencedCodeBlock) {
                     executionSteps = fencedCodeBlock.getLiteral();
                  }
               }
            }
         }
      }

      return new Runbook(id, title, description, tags, executionSteps, requiredVars);
   }

   @NotNull
   private static List<String> getSteps(OrderedList ol) {
      List<String> stepsList = new ArrayList<>();
      for (Node li = ol.getFirstChild(); li != null; li = li.getNext()) {
         if (li instanceof ListItem liNode) {
            // Get literal text for each step item
            Node stepContent = liNode.getFirstChild();
            if (stepContent instanceof Paragraph pStep && pStep.getFirstChild() instanceof Text stepText) {
               stepsList.add(stepText.getLiteral());
            } else {
               stepsList.add(stepContent.toString().trim());
            }
         }
      }
      return stepsList;
   }
}