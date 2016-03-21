This is a system for specifying hierarchical properties. It provides application developers the following benefits:
  * Distribution of properties among multiple files and directories to allow separation of general and user-specific values, as well as grouping related properties. Files are identified by URLs and can therefore be read from archives or remote locations.
  * Support for many data types, listed below, as well as extensibility for custom types. This avoids doing the repetitive task of parsing and converting strings after reading and before writing.
  * The API is easy to use. Normally, it requires only to use the PropertyTree class, which provides convenience read- and write methods for all the data types supported by default, and a bean population method which can interpret a property tree as a Java Bean. All read methods optionally support default values. More direct access to the API is only needed to insert new properties, not to update existing ones. PropertyTree can be subclassed to implement additional utility features, such as localization.

The project includes an implementation of the API, using DOM4J. Note that this is independent from the file format. DOM4J is only used to internally represent the tree. Different file formats can be plugged in just as custom types.
Currently, the only file format is PlainProperties, a modified .properties format and does not have save support.


# Types #

There are several types that may be used to specify the contents of a property:
  * `include`
> this type includes another properties file in the tree. The property's content is the file's path. As all file formats use the same internal structure, different file formats can be mixed with includes
  * `literal-string`
> this type is used for unparsed String content
  * `string`
> this type is used for String content, replacing `%{`...`}` tree property references and `${`...`}` system property references. This type is assumed if no other is given
  * `boolean`, `byte`, `char`, `short`, `int`, `long`, `float`, `double`
> these are the java primitive types, and are converted through the `parseXXX` and `toString` methods of corresponding classes.
  * `integer`, `decimal`
> these are the types `BigInteger` and `BigDecimal` for storing arbitrarily sized numeric values
  * `date`
> these store time values as unix timestamps (decimal number of milliseconds since 1970-01-01 00:00) and are returned as `long`, `Date` or `Calendar`
  * `path`
> This is used to store paths. If relative, the path is relative to the property's location. Paths are internally processed as `URL`s, but can alternatively be returned as `File`s or `URI`s
  * `xml`
> This type is used for storing arbitrary xml data. It can be returned as (DOM4J) `Element` or `Document` objects. Note that since the internal data structure uses XML, this type can be ambiguous; you might have to use `literal-string|xml` or `string|xml`. See "Piping" below.
  * `pattern`
> This type is used to store regular expression patterns and are returned as `Pattern` objects.
  * `color`
> Stores a RGB color as six hexadecimal digits
  * `class`
> Stores java class names
  * `plain-properties`
> Stores key-value mappings in the PlainProperties format
  * `base64`
> Stores arbitrary base64-encoded binary data. Can be returned as `InputStream` or `byte[]`.

In addition to these types, the TreeProperties file format has a `tree-properties` type which parses its value like the `plain-properties` type, but treats the result as a new branch in the property tree

# Example #

These examples specify 3 string properties with the values: `"hello"`, `"world"`, `"!"`. The last is in a property with empty name. The mappings are the following:

```
/root/ex1      --> hello
/root/sub1/ex2 --> world
/root/ex3      --> !
```

### Simple example ###

```
root/ex1=hello
root/sub1/ex2=world
root/ex3=!
```

### Using `tree-properties` ###

```
root--tree-properties=<<<0
    ex1=hello
    sub1/ex2=world
    ex3=!
0>>>
```

Note that `<<<0` and `0>>>` are part of the PlainProperties file format and not part of the `tree-properties` type. You could represent the same property mappings using escapes, although much less readable:

```
root--tree-properties=ex1=hello \n sub1/ex2=world \n ex3=!
```

### Using `include` ###

root.properties
```
root/ex1=hello
root/sub1--include=sub.properties
root/ex3=!
```

sub.properties
```
ex2=world
```

# Piping #

With piping, you can arbitrarily chain conversions of different types. Although the standard types usually don't need this, it can be practical in certain occasions. Note that most types perform implicit piping when encountering `Element` values (which is the default in the internal XML structure). so, the type `int` behaves equivalent to `literal-string|string|int` when encountering an `Element`.

Piping is especially important with the `xml` type. As the raw value is already an `Element`, the `xml` type will return it without piping, which is reasonable considering that the file format could natively use XML:

```
test--xml=<hello><world/></hello>
```

will result in

```
<value>&lt;hello&gt;&lt;world/&gt;&lt;/hello&gt;</value>
```

whereas

```
test--string|xml=<hello><world/></hello>
```

will result in

```
<hello>
 <world></world>
</hello>
```

XML can also be read from an Input stream, making `base64|xml` another reasonable choice.

# Beans using PropertyTree #

The PropertyTree class can parse a property hierarchy as a Java Bean, reflectively using appropriate setter methods. For example, reading this File as a bean:

```
button--tree-properties=<<<0
    class=javax.swing.JButton
    
    text=Button
    background--color=FF0000
0>>>
```

Would create a red JButton with "Button" as its text. The bean read mechanism currently uses get and set methods only and does not support direct attribute access. It works recursively, so you can populate fields that take other beans and not natively supported types consider this "linked list":

```
list--tree-properties=<<<0
    class=LinkedEntry

    value=first
    next--tree-properties=<<<1
        class=LinkedEntry

        value=second
        next--tree-properties=<<<2
            class=LinkedEntry

            value=third
        2>>>
    1>>>
0>>>
```

As you can see, this is neither an optimal nor an elegant solution to such a problem, and it is not the problem this mechanism is meant for. To store complex java objects, use `base64` and serialization (which will be a standard type in the future) or XMLEncoder.

This mechanism can be used, however, in other places. For example, the Utils subproject uses it to read configuration files. The beans read this way are very simple setup-run-dispose objects, for which this is a good solution.