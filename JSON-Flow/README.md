JSON Flow (JSF)
===

# Global Idea

Representing non bounded flow of JSON values.

Interesting use cases:
- live feed of log entries represented by JSON objects
- updates received on a websocket, potentially buffered by number of bytes and not by objects

Ideas in mind for design of the specification:
- create a subset of JSON, where normal JSON is completely compatible and can be used as is
- there is no root object or array, just values flowing
- get rid of all characters that are not absolutely needed (keep them to an absolute minimum)
- support comments

# JSFT - JSON Flow of Text

|                       |                      |
|-----------------------|----------------------|
| Acronym               | JSFT                 |
| Name                  | JSON Flow of Text    |
| Media Type - Actual   | `application/x-jsft` |
| Media Type - Proposed | `application/jsft`   |
| File Extension        | `.jsft`              |
| Type                  | Text                 |
| Encoding              | UTF-8 by default     |

Represent a flow of JSON values as text. This representation is still human readable, but a 
more flexible and contracted than JSON.

## Format

All those concepts are illustrated in the example below.

### Root Element

There is no need for a "root element" (typically an _Object_ or an _Array_ in JSON) since
the flow is not defined in size. Values are directly stated from the first character of the
stream or file.

### Separators

Values are separated by one of the following characters. They can be repeated and would still
be considered as a single separator.

#### Space

(` `)

Example:
```
1 2 3 4 true false "asd" {a:"b"} [1,2,3]
```
is
```json
[1, 2, 3, 4, true, false, "asd", {"a":"b"}, [1,2,3]]
```

#### Tab

(`\t`)

Example, where `⇥` represent tabs:
```
1⇥2⇥3⇥4⇥true⇥false⇥"asd"⇥{a:"b"}⇥[1,2,3]
```
is
```json
[1, 2, 3, 4, true, false, "asd", {"a":"b"}, [1,2,3]]
```

#### Line Feed

(`\n` or `\r`, including `\r\n`)

```
1
2
3
4
true
false
"asd"
{a:"b"}
[1,2,3]
```
is
```json
[1, 2, 3, 4, true, false, "asd", {"a":"b"}, [1,2,3]]
```

#### Boundaries or an array

(`[` or `]`)

Example:
```
1 2["a","b"]"foo"
```
is
```json
[1, 2, ["a", "b"], "foo"]
```

#### Boundaries or an object

(`{` or `}`)

Example:
```
1 2{a:1,b:2}true
```
is
```json
[1, 2, {"a": 1, "b": 2}, true]
```

#### Boundaries of a comment

(`/*` or `*/` or `//`)

Example:
```
1 2/*I am Groot*/3 4//Foo
```
is
```json
[1, 2, {"$comment": "I am Groot"}, 3, 4, {"$comment": "Foo"}]
```

#### Boundaries of a string

When logically applicable.

Example:
```
1 true"I am String"3 4
```
is
```json
[1, true, "I am String", 3, 4]
```

### Comments

Comments are supported in JSFT.

Any `/*` outside of a string value begins a comment section. During such a section, separators
are no longer applied and are included in the text of the comment. The next `*/` ends the comment
section.

Since standard JSON does not support comments, such a comment would be converted to a JSON object
with only one `$comment` property with the content of the comment as its value.

## Example

This _JSFT_

```
24 3.124543[3,2,123]true null false{foo:"I am Groot", bar:3,1416}/* comments are supported */ 435654.2343 "bleh"
"aaa/*bbb*/ccc"
"bleu" // rest of line comments are also supported
"lapin"
// this entire paragraph
// would be considered "one comment"
// with linefeeds in it
42
```

would translate to _JSON_ as

```json
[
    24,
    3.124543,
    [
        3,
        2,
        123
    ],
    true,
    null,
    false,
    {
        "foo": "I am Groot",
        "bar: 3.1416
    },
    {
        "$comment": " comments are supported "
    },
    435654.2343,
    "bleh",
    "aaa/*bbb*/ccc",
    "bleu",
    {
        "$comment": " rest of line comments are also supported"
    },
    "lapin",
    {
        "$comment": " this entire paragraph\n would be considered \"one comment\" with linefeeds in it"
    },
    42
]
```

# JSFT - JSON Flow of Bytes

|                       |                      |
|-----------------------|----------------------|
| Acronym               | JSFB                 |
| Name                  | JSON Flow of Bytes   |
| Media Type - Actual   | `application/x-jsfb` |
| Media Type - Proposed | `application/jsfb`   |
| File Extension        | `.jsfb`              |
| Type                  | Binary               |
| Encoding              | To be determined     |

A binary representation of the JSON Flow of Values

## Encoding

To be determined. But should stick to one of the established well known
binary representations encoding.

Candidates are:
- [Protocol Buffer](https://developers.google.com/protocol-buffers/)
- [Cap'n Proto](https://capnproto.org/)

## Feature Brainstorming

### Use encoding for typing

_Protocol Buffer_ and _Capt'n Proto_ both have a bit-wise way of describing the
type of a value. Use that for knowing the type of the incoming value instead of
redifining one of our own.

### Stay DRY on property names

Since the idea of the binary representation is to as concise as possible, the idea
here would be to never repeat the name of property as text. The idea would be
as follow.

- the first time a property name is encountered, encode a specific value that communicates
  to the consumer the name of the next incoming property accompanied by a numeric ID.
- the consumer has to remember that ID and the corresponding textual name of the property,
  making the stream "context based" (or "session based").
- later on, when the same property name is encounter, the producer sends the numeric ID
  instead of the full name of the property, followed by its value.

Example:
- begin object
    - property "first_name" is 1001
        - value is "Joe"
    - property "last_name" is 1002
        - value is "Dassin"
    - property "discography" is 1003
        - value is ["I do not know", "The Albums That", "Joe Dassin Recorded"]
- end object
- begin object
    - property 1001
        - value is "Usain"
    - property 1002
        - value is "Bolt"
    - property "medals" is 1004
        - value is [ "a", "b", "c" ]
- end object

Over large streams, this would saves tons of bytes on the wire and minimise
large files' size considerably, while still keeping a JSON like organisation
of the data.

The example above would translate as the following JSFT (wrapping at the end of objects for readability).

```
{"first_name":"Joe","last_name":"Dassin","discography":["I do not know", "The Albums That", "Joe Dassin Recorded"]}
{"first_name":"Usain","last_name":"Bolt","medals":["a","b","c"]}
```

Or as the following JSON.

```json
[
    {
        "first_name": "Joe",
        "last_name": "Dassin",
        "discography": [
            "I do not know",
            "The Albums That",
            "Joe Dassin Recorded"
        ]
    },
    {
        "first_name": "Usain",
        "last_name": "Bolt",
        "medals": [
            "a",
            "b",
            "c"
        ]
    }
]
```
