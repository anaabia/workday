# Address Book Application

AddressBook is a simple application that associates email addresses with names (see Book interface). A simple explanation about the application architecture is:

* A name is either an alias or a group
* An alias maps to an email address directly or to another name (forming a name chain)
* A group is like an alias but it can refer to one or more addresses or names
* Any name belonging to the book must map (directly or indirectly) to at least one address
* A name cannot be removed from the book if that name is the target of another mapping (e.g. alias in group)

## Exercises

### 1. Correct Failing Tests

Running StandardBookTests as provided results in failures. Your first task is to add the necessary validation checks to make the 
tests pass (see Target class for an example)

### 2. Extend Book

BookExt interface contains two new methods. Your task is now to:

1. Implement both methods in the existing StandardBook class
2. Add tests for these methods in StandardBookTests

### 3. Fix a Bug

A user of the book application has reported that he gets a java.lang.StackOverflowError
when doing a lookup on a group. He attaches some debug output of a book that generates 
the error (see below).

1. Write a test to reproduce the problem
2. Modify the book class to fix the problem so all tests run successfully and class fulfills the contract of the Book interface

```
{Book 
 {Group 'Manager'} => [{Alias 'Jim'}, {Group 'Fire Marshal'}, {Alias 'Michael'}]
 {Alias 'Jim'} => [{Address 'jim.halpert@theoffice.com'}]
 {Group 'Fire Marshal'} => [{Group 'Manager'}, {Alias 'Dwight'}]
 {Alias 'Dwight'} => [{Address 'dwight.schrute@theoffice.com'}]
 {Alias 'Michael'} => [{Address 'michael.scott@theoffice.com'}]
}EndBook
```

Your next task is to fix the problem reported by the user.

### 4. Optimize Book

Your task is now to rewrite the lookup methods so that neither method uses recursion.

## Evaluation Criteria

Your implementation will be evaluated against the following criterias:

* Unit test coverage
* Code cleanliness
* Code cohesion and couplinbg
* Unnecessary checks, variables and data structures created
* Performance
* Code coupling and cohesion

**When the code is submitted we assume that it would be production ready and we evaluate what would be the outcome of that.**

In case of questions you can contact us and we will help you as soon as possible. **Good luck and have a nice coding. :-)**