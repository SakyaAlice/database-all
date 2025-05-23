package gaarason.database.test.parent;

import gaarason.database.contract.connection.GaarasonDataSource;
import gaarason.database.contract.eloquent.Builder;
import gaarason.database.contract.eloquent.Record;
import gaarason.database.contract.eloquent.RecordList;
import gaarason.database.test.models.relation.model.StudentModel;
import gaarason.database.test.models.relation.pojo.Student;
import gaarason.database.test.parent.base.BaseTests;
import gaarason.database.util.ObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.Collections;
import java.util.List;

@Slf4j
@FixMethodOrder(MethodSorters.JVM)
abstract public class SerializableTests extends BaseTests {

    private static final StudentModel studentModel = new StudentModel();

    @Override
    protected GaarasonDataSource getGaarasonDataSource() {
        return studentModel.getGaarasonDataSource();
    }

    @Override
    protected List<TABLE> getInitTables() {
        return Collections.singletonList(TABLE.student);
    }

    @Test
    public void Record序列化_deepCopy() {
        Record<Student, Long> record = studentModel.findOrFail(2);

        Record<Student, Long> recordCopy = ObjectUtils.deepCopy(record);
        System.out.println(recordCopy);

        Student object = recordCopy.toObject();
        Assert.assertEquals(2, object.getId().intValue());
    }

    @Test
    public void RecordList序列化_deepCopy() {
        RecordList<Student, Long> records = studentModel.newQuery().get();
        Assert.assertEquals(10, records.size());
        List<Student> students = records.toObjectList();
        System.out.println(students);
        Assert.assertNotNull(students);
        System.out.println(students.size());
        Assert.assertEquals(10, students.size());


        RecordList<Student, Long> recordsCopy = ObjectUtils.deepCopy(records);
        System.out.println(recordsCopy);
        Assert.assertEquals(10, recordsCopy.size());

        List<Student> object = recordsCopy.toObjectList();
        System.out.println(object);
        Assert.assertNotNull(object);
        System.out.println(object.size());
        Assert.assertEquals(10, object.size());
    }

    @Test
    public void Builder序列化_deepCopy() {
        Builder<?, Student, Long> builder = studentModel.newQuery().where(Student::getId, 4);
        Builder<?, Student, Long> deepCopyBuilder = ObjectUtils.deepCopy(builder);
        Student student = deepCopyBuilder.firstOrFail().toObject();
        Assert.assertEquals(4, student.getId().intValue());
    }

    @Test
    public void Record序列化_serializeToString() {
        Student student1 = new Student();
        student1.setAge(12);

        Record<Student, Long> record = studentModel.findOrFail(2).with("teachersBelongsToMany", b -> {
            return b.limit(student1.getAge());
        });

        String serialize = record.serializeToString();
        System.out.println(serialize);
        Assert.assertFalse(ObjectUtils.isEmpty(serialize));
        System.out.println(serialize.length());


        Student student = record.toObject();
        Assert.assertNotNull(student.getTeachersBelongsToMany());

        Record<Student, Long> recordCopy = Record.deserialize(serialize);

        System.out.println(recordCopy);

        Student object = recordCopy.toObject();
        System.out.println(recordCopy);
        System.out.println(recordCopy.toString().length());
        System.out.println(object);
        System.out.println(object.toString().length());
        Assert.assertEquals(2, object.getId().intValue());
        Assert.assertNotNull(object.getTeachersBelongsToMany());
    }

    @Test
    public void newTest() {
        studentModel.newQuery().with(Student::getTeacher, builder -> (Builder<?, ?, ?>) builder).get().toObjectList();
    }

    @Test
    public void RecordList序列化_serializeToString() {
        Student student1 = new Student();
        student1.setAge(3);

        RecordList<Student, Long> records = studentModel.newQuery()
                .with("teachersBelongsToMany", builder -> builder.limit(1)).get();

        String serialize = records.serializeToString();
        System.out.println(serialize);
        Assert.assertFalse(ObjectUtils.isEmpty(serialize));
        System.out.println(serialize.length());


        List<Student> student = records.toObjectList();
        Assert.assertFalse(ObjectUtils.isEmpty(student));
        Assert.assertNotNull(student.get(0).getTeachersBelongsToMany());

        RecordList<Student, Long> recordsCopy = RecordList.deserialize(serialize);

        System.out.println(recordsCopy);

        List<Student> objects = recordsCopy.toObjectList();
        System.out.println(recordsCopy);
        System.out.println(recordsCopy.toString().length());
        System.out.println(objects);
        System.out.println(objects.toString().length());
        Assert.assertFalse(ObjectUtils.isEmpty(objects));
        Assert.assertNotNull(objects.get(0).getTeachersBelongsToMany());
    }

    @Test
    public void builder序列化_serializeToString() {
        Student student1 = new Student();
        student1.setAge(3);

        Builder<?, Student, Long> builder = studentModel.newQuery().with("teachersBelongsToMany", b -> {
            return b.limit(student1.getAge());
        });

        String serialize = builder.serializeToString();
        System.out.println(serialize);
        Assert.assertFalse(ObjectUtils.isEmpty(serialize));
        System.out.println(serialize.length());


        List<Student> student = builder.get().toObjectList();
        Assert.assertFalse(ObjectUtils.isEmpty(student));
        Assert.assertNotNull(student.get(0).getTeachersBelongsToMany());

        Builder<?, Student, Long> builderCopy = Builder.deserialize(serialize);

        System.out.println(builderCopy);

        List<Student> objects = builderCopy.get().toObjectList();
        System.out.println(objects);
        System.out.println(objects.toString().length());
        Assert.assertFalse(ObjectUtils.isEmpty(objects));
        Assert.assertNotNull(objects.get(0).getTeachersBelongsToMany());
    }

    @Test
    public void Record序列化_serialize() {
        Record<Student, Long> record = studentModel.findOrFail(2);

        byte[] serialize = record.serialize();
        Assert.assertFalse(ObjectUtils.isEmpty(serialize));
        System.out.println(serialize.length);
        System.out.println(serialize);

        Record<Student, Integer> recordCopy = Record.deserialize(serialize);

        System.out.println(recordCopy);

        Student object = recordCopy.toObject();
        Assert.assertEquals(2, object.getId().intValue());
    }

    /**
     * @see #Record序列化_serializeToString
     */
    @Test
    public void Record反序列化() {
        String s = "rO0ABXNyACVnYWFyYXNvbi5kYXRhYmFzZS5lbG9xdWVudC5SZWNvcmRCZWFuAAAAAAAAAAEMAAB4cHdDAAZteXNxbDIAOWdhYXJhc29uLmRhdGFiYXNlLnRlc3QubW9kZWxzLnJlbGF0aW9uLm1vZGVsLlN0dWRlbnRNb2RlbHNyABFqYXZhLnV0aWwuSGFzaE1hcAUH2sHDFmDRAwACRgAKbG9hZEZhY3RvckkACXRocmVzaG9sZHhwP0AAAAAAAAx3CAAAABAAAAAIdAAKaXNfZGVsZXRlZHNyABFqYXZhLmxhbmcuQm9vbGVhbs0gcoDVnPruAgABWgAFdmFsdWV4cAB0AAp1cGRhdGVkX2F0c3IADmphdmEudXRpbC5EYXRlaGqBAUtZdBkDAAB4cHcIAAABKDAoPNh4dAAKdGVhY2hlcl9pZHNyAA5qYXZhLmxhbmcuTG9uZzuL5JDMjyPfAgABSgAFdmFsdWV4cgAQamF2YS5sYW5nLk51bWJlcoaslR0LlOCLAgAAeHAAAAAAAAAABnQAA3NleHNyABFqYXZhLmxhbmcuSW50ZWdlchLioKT3gYc4AgABSQAFdmFsdWV4cQB-AAwAAAACdAAEbmFtZXQABuWwj-W8oHQACmNyZWF0ZWRfYXRzcQB-AAh3CAAAASAD1cb4eHQAAmlkc3EAfgALAAAAAAAAAAJ0AANhZ2VzcQB-AA8AAAALeHNxAH4AAj9AAAAAAAAMdwgAAAAQAAAACHEAfgAEcQB-AAZxAH4AB3EAfgAJcQB-AApxAH4ADXEAfgAOcQB-ABBxAH4AEXEAfgAScQB-ABNxAH4AFHEAfgAVcQB-ABZxAH4AF3EAfgAYeHoAAAIxAi9zZWxlY3QgYHN0dWRlbnRfOTY0MTUwMDUzYC5gbmFtZWAsYHN0dWRlbnRfOTY0MTUwMDUzYC5gYWdlYCxgc3R1ZGVudF85NjQxNTAwNTNgLmBzZXhgLGBzdHVkZW50Xzk2NDE1MDA1M2AuYHRlYWNoZXJfaWRgLGBzdHVkZW50Xzk2NDE1MDA1M2AuYGlzX2RlbGV0ZWRgLGBzdHVkZW50Xzk2NDE1MDA1M2AuYGNyZWF0ZWRfYXRgLGBzdHVkZW50Xzk2NDE1MDA1M2AuYHVwZGF0ZWRfYXRgLGBzdHVkZW50Xzk2NDE1MDA1M2AuYGlkYCBmcm9tIGBzdHVkZW50YCBhcyBgc3R1ZGVudF85NjQxNTAwNTNgIHdoZXJlIChgc3R1ZGVudF85NjQxNTAwNTNgLmBpc19kZWxldGVkYD0gPyAgYW5kIGBzdHVkZW50Xzk2NDE1MDA1M2AuYGlkYGluKHNlbGVjdCBgdGVhY2hlcl81ODkyMTI0NjBgLmBpZGAgZnJvbSBgdGVhY2hlcmAgYXMgYHRlYWNoZXJfNTg5MjEyNDYwYCkgb3IgKGBzdHVkZW50Xzk2NDE1MDA1M2AuYGlzX2RlbGV0ZWRgPSA_ICBhbmQgMSkpIGFuZCBgc3R1ZGVudF85NjQxNTAwNTNgLmBpc19kZWxldGVkYD0gPyAgYW5kIGBzdHVkZW50Xzk2NDE1MDA1M2AuYGlkYD0gPyAgbGltaXQgID8gc3EAfgACP0AAAAAAAAx3CAAAABAAAAABdAAVdGVhY2hlcnNCZWxvbmdzVG9NYW55c3IAM2dhYXJhc29uLmRhdGFiYXNlLmNvbnRyYWN0LmVsb3F1ZW50LlJlY29yZCRSZWxhdGlvbgAAAAAAAAABAgAFWgARcmVsYXRpb25PcGVyYXRpb25MAA1jdXN0b21CdWlsZGVydAA3TGdhYXJhc29uL2RhdGFiYXNlL2NvbnRyYWN0L2Z1bmN0aW9uL0J1aWxkZXJBbnlXcmFwcGVyO0wAEG9wZXJhdGlvbkJ1aWxkZXJxAH4AHUwADXJlY29yZFdyYXBwZXJ0ADNMZ2FhcmFzb24vZGF0YWJhc2UvY29udHJhY3QvZnVuY3Rpb24vUmVjb3JkV3JhcHBlcjtMABFyZWxhdGlvbkZpZWxkTmFtZXQAEkxqYXZhL2xhbmcvU3RyaW5nO3hwAHNyACFqYXZhLmxhbmcuaW52b2tlLlNlcmlhbGl6ZWRMYW1iZGFvYdCULCk2hQIACkkADmltcGxNZXRob2RLaW5kWwAMY2FwdHVyZWRBcmdzdAATW0xqYXZhL2xhbmcvT2JqZWN0O0wADmNhcHR1cmluZ0NsYXNzdAARTGphdmEvbGFuZy9DbGFzcztMABhmdW5jdGlvbmFsSW50ZXJmYWNlQ2xhc3NxAH4AH0wAHWZ1bmN0aW9uYWxJbnRlcmZhY2VNZXRob2ROYW1lcQB-AB9MACJmdW5jdGlvbmFsSW50ZXJmYWNlTWV0aG9kU2lnbmF0dXJlcQB-AB9MAAlpbXBsQ2xhc3NxAH4AH0wADmltcGxNZXRob2ROYW1lcQB-AB9MABNpbXBsTWV0aG9kU2lnbmF0dXJlcQB-AB9MABZpbnN0YW50aWF0ZWRNZXRob2RUeXBlcQB-AB94cAAAAAZ1cgATW0xqYXZhLmxhbmcuT2JqZWN0O5DOWJ8QcylsAgAAeHAAAAABc3IAM2dhYXJhc29uLmRhdGFiYXNlLnRlc3QubW9kZWxzLnJlbGF0aW9uLnBvam8uU3R1ZGVudLlX1DSoKDR7AgAMTAADYWdldAATTGphdmEvbGFuZy9JbnRlZ2VyO0wACWNyZWF0ZWRBdHQAEExqYXZhL3V0aWwvRGF0ZTtMAAlpc0RlbGV0ZWR0ABNMamF2YS9sYW5nL0Jvb2xlYW47TAAEbmFtZXEAfgAfTAAacmVsYXRpb25zaGlwU3R1ZGVudFRlYWNoZXJ0AEhMZ2FhcmFzb24vZGF0YWJhc2UvdGVzdC9tb2RlbHMvcmVsYXRpb24vcG9qby9SZWxhdGlvbnNoaXBTdHVkZW50VGVhY2hlcjtMABtyZWxhdGlvbnNoaXBTdHVkZW50VGVhY2hlcnN0ABBMamF2YS91dGlsL0xpc3Q7TAAEc2VsZnQANUxnYWFyYXNvbi9kYXRhYmFzZS90ZXN0L21vZGVscy9yZWxhdGlvbi9wb2pvL1N0dWRlbnQ7TAADc2V4cQB-AChMAAd0ZWFjaGVydAA1TGdhYXJhc29uL2RhdGFiYXNlL3Rlc3QvbW9kZWxzL3JlbGF0aW9uL3Bvam8vVGVhY2hlcjtMAAl0ZWFjaGVySWR0ABBMamF2YS9sYW5nL0xvbmc7TAAVdGVhY2hlcnNCZWxvbmdzVG9NYW55cQB-ACxMAAl1cGRhdGVkQXRxAH4AKXhyADtnYWFyYXNvbi5kYXRhYmFzZS50ZXN0Lm1vZGVscy5yZWxhdGlvbi5wb2pvLmJhc2UuQmFzZUVudGl0eQAAAAAAAAABAgABTAACaWRxAH4AL3hwcHNxAH4ADwAAAAxwcHBwcHBwcHBwcHZyAC9nYWFyYXNvbi5kYXRhYmFzZS50ZXN0LnBhcmVudC5TZXJpYWxpemFibGVUZXN0cwAAAAAAAAAAAAAAeHB0ADVnYWFyYXNvbi9kYXRhYmFzZS9jb250cmFjdC9mdW5jdGlvbi9CdWlsZGVyQW55V3JhcHBlcnQAB2V4ZWN1dGV0AFwoTGdhYXJhc29uL2RhdGFiYXNlL2NvbnRyYWN0L2Vsb3F1ZW50L0J1aWxkZXI7KUxnYWFyYXNvbi9kYXRhYmFzZS9jb250cmFjdC9lbG9xdWVudC9CdWlsZGVyO3QAL2dhYXJhc29uL2RhdGFiYXNlL3Rlc3QvcGFyZW50L1NlcmlhbGl6YWJsZVRlc3RzdAAzbGFtYmRhJFJlY29yZOW6j-WIl-WMll9zZXJpYWxpemVUb1N0cmluZyQ5OTIxODdkNSQxdACRKExnYWFyYXNvbi9kYXRhYmFzZS90ZXN0L21vZGVscy9yZWxhdGlvbi9wb2pvL1N0dWRlbnQ7TGdhYXJhc29uL2RhdGFiYXNlL2NvbnRyYWN0L2Vsb3F1ZW50L0J1aWxkZXI7KUxnYWFyYXNvbi9kYXRhYmFzZS9jb250cmFjdC9lbG9xdWVudC9CdWlsZGVyO3EAfgA3c3EAfgAhAAAABnVxAH4AJQAAAAB2cgA1Z2FhcmFzb24uZGF0YWJhc2UuY29udHJhY3QuZnVuY3Rpb24uQnVpbGRlckFueVdyYXBwZXJu5LFODqdfPwIAAHhwcQB-ADVxAH4ANnEAfgA3cQB-ADV0ABhsYW1iZGEkc3RhdGljJGJkZTFhZDBlJDFxAH4AN3EAfgA3c3EAfgAhAAAABnVxAH4AJQAAAAB2cQB-AAB0ADFnYWFyYXNvbi9kYXRhYmFzZS9jb250cmFjdC9mdW5jdGlvbi9SZWNvcmRXcmFwcGVycQB-ADZ0AFooTGdhYXJhc29uL2RhdGFiYXNlL2NvbnRyYWN0L2Vsb3F1ZW50L1JlY29yZDspTGdhYXJhc29uL2RhdGFiYXNlL2NvbnRyYWN0L2Vsb3F1ZW50L1JlY29yZDt0ACVnYWFyYXNvbi9kYXRhYmFzZS9lbG9xdWVudC9SZWNvcmRCZWFudAAWbGFtYmRhJHdpdGgkN2E3ZDVmYTckMXEAfgBEcQB-AERxAH4AG3h4";
        Record<Student, Long> recordCopy = Record.deserialize(s);
        System.out.println(recordCopy);

        Student object = recordCopy.toObject();
        System.out.println(recordCopy);
        System.out.println(recordCopy.toString().length());
        System.out.println(object);
        System.out.println(object.toString().length());
        Assert.assertEquals(2, object.getId().intValue());
        Assert.assertNotNull(object.getTeachersBelongsToMany());


    }
//
//    // 抽象父类
//    public static abstract class Builder<B extends Builder<B, T, K>, T, K> {
//        // 泛型方法，返回调用它的类的实例类型
//        public abstract B getSelf();
//
//        // 其他父类方法...
//    }
//
//    // 子类
//    public static class MySqlBuilderV2<T, K> extends Builder<MySqlBuilderV2<T, K>, T, K > {
//        @Override
//        public MySqlBuilderV2<T, K> getSelf() {
//            return this;
//        }
//
//        // 子类特有的方法...
//    }
//
//    public void ttt() {
//        MySqlBuilderV2<Object, Object> objectObjectMySqlBuilderV2 = new MySqlBuilderV2<>();
//
//
//    }

}
