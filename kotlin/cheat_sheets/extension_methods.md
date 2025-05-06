# Kotlin Extension Methods

## At a glance

Where `r` is the receiver.

| ⬇️ returns \ ➡️ receiver/param | `this` (`T.()`)                                                                                              | `it` (`(T)`)                                                                | Standalone                                                                                         |
| ------------------------------ | ------------------------------------------------------------------------------------------------------------ | --------------------------------------------------------------------------- | -------------------------------------------------------------------------------------------------- |
| return of the lambda           | `r.run { }` ([doc](https://kotlinlang.org/docs/scope-functions.html#run))                                    | `r.let { }` ([doc](https://kotlinlang.org/docs/scope-functions.html#let))   | `run { }` ([doc](https://kotlinlang.org/docs/scope-functions.html#run))                            |
|                                | `with(r) { }` ([doc](https://kotlinlang.org/docs/scope-functions.html#with))                                 |                                                                             |                                                                                                    |
| the object itself              | `r.apply { }` ([doc](https://kotlinlang.org/docs/scope-functions.html#apply))                                | `r.also { }` ([doc](https://kotlinlang.org/docs/scope-functions.html#also)) |                                                                                                    |
| the object itself or `null`    | `r.takeIf { predicate }` ([doc](https://kotlinlang.org/docs/scope-functions.html#takeif-and-takeunless))     |                                                                             |                                                                                                    |
|                                | `r.takeUnless { predicate }` ([doc](https://kotlinlang.org/docs/scope-functions.html#takeif-and-takeunless)) |                                                                             |                                                                                                    |
| Unit                           |                                                                                                              |                                                                             | `repeat(times) { i -> }` ([doc](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/repeat.html)) |

## Examples

### run (with receiver)

#### Procedural version

```kotlin
val generator = PasswordGenerator()
generator.seed = "some string"
generator.hash = { s -> someHash(s) }
generator.hashRepetitions = 1000

val password = generator.generate()
```

#### The Kotlin Way

```kotlin
val password = PasswordGenerator().run {
    seed = "some string"
    hash = { s -> someHash(s) }
    hashRepetitions = 1000

    generate()
}
```

### apply

#### Procedural version

```kotlin
val generator = PasswordGenerator()
generator.seed = "some string"
generator.hash = { s -> someHash(s) }
generator.hashRepetitions = 1000

App(generator).run()
```

#### The Kotlin Way

```kotlin
val generator = PasswordGenerator.apply {
    seed = "some string"
    hash = { s -> someHash(s) }
    hashRepetitions = 1000
}

App(generator).run()
```
