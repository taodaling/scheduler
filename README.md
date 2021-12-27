# Usage

It's a framework for multi purpose. You can regard it as an extendable competitve programming tool which provide below facilities:

- Inline code into one file
- Download task metadata from competitive-companion
- Test your code against those tests included in task metadata

It supports C++, Rust for now.

# How to use

It's very easy to use, modify the scheduler.json to what you like, then pacakge the project

```sh
# mvn package
```

Then launch the Scheduler

```sh
# java -cp libs/* scheduler.Scheduler
```

# C++

The inline facility is implemented in such way: Replacing all the header surrounded by double quotes with matching file. 

# Rust

The inline facility is achieved by Regular expression, it finds all the used libraries and inline needed mod into same file. But the inline mechanism requires some constraints on your code.
