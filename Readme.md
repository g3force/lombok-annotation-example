# Custom Lombok Annotation Example

This project demonstrates how a custom Lombok annotation can be implemented.

## Sub-Projects

* **api**: Contains the annotation, required by the consumer at compile time.
* **processor**: Contains the Lombok annotation processor that hooks into the compiler to modify all annotated methods.
* **example**: Consumer module that uses the Annotation.

## Usage

To run the examples, run: `./gradlew run`

## Notes

* The Gradle daemon seems to have strange effects on the annotation processor, so it is disabled for this project.
* IntelliJ uses a Gradle daemon even though it is disabled, so you may need to stop it manually, if you run into errors.
