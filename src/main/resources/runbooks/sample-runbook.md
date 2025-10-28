## Runbook ID

sample-runbook-1

## Title

A Sample Runbook

## Description

This is a sample runbook. You need to have tags that the vector database will require embeddings, and execution steps.

## Tags

- Tag 1
- Tag 2
- Tag 3
- Tag 4

## Required Variables

- orderId

## Execution Steps

```
1. Generate a new Authentication token
2. Use this token and invoke N8N webhook called sampleWebhook1.
{
    "queue": "queue name",
    "message": "string", 
    "action": "comment/close"
}
Use the action to find the correct method in the zendesk too to invoke. Pass the message as is and the queue name to the method.  
```