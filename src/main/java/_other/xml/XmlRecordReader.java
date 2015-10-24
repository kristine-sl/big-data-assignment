package _other.xml;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.io.DataOutputBuffer;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class XmlRecordReader extends RecordReader< LongWritable, Text > {

    private long start;
    private long end;

    private DataOutputBuffer buffer;
    private FSDataInputStream input;

    private LongWritable key;
    private Text value;

    private String startTag, endTag;

    public XmlRecordReader( String tag ) {

        startTag = "<" + tag + ">";
        endTag = "</" + tag + ">";
    }

    @Override
    public void initialize( InputSplit split, TaskAttemptContext context ) throws IOException, InterruptedException {

        FileSplit fileSplit = ( FileSplit ) split;

        start = fileSplit.getStart();
        end = start + fileSplit.getLength();

        Configuration config = context.getConfiguration();

        FileSystem fileSystem = fileSplit.getPath().getFileSystem( config );

        buffer = new DataOutputBuffer();
        input = fileSystem.open( fileSplit.getPath() );
        input.seek( start );

        boolean tagFound = false;
        int i = 0;

        // Gets input past root tag
        while ( !tagFound ) {

            int in = input.read();

            if ( in == ( byte ) startTag.charAt( i ) ) i++;
            else i = 0;

            if ( i == startTag.length() ) tagFound = true;
        }
    }

    @Override
    public boolean nextKeyValue() throws IOException, InterruptedException {

        String currentTag = "";

        int temp = input.read();

        while ( temp != '<' ) {

            temp = input.read();
        }


        if ( temp == ( byte ) '<' ) {

            buffer.write( '<' );

            currentTag = getCurrentTag();

        }

        boolean tag = false, closing = false;

        while ( true ) {

            if ( input.getPos() >= end ) return false;

            int in = input.read();

            if ( in == -1 ) return false;

            buffer.write( in );

            if ( in == ( byte ) '<' ) tag = true;

            if ( tag && in == ( byte ) '/' ) closing = true;

            if ( closing ) {

                String current = getCurrentTag();

                if ( current.equals( currentTag ) ) {

                    key = new LongWritable( input.getPos() );
                    value = new Text( minimizeOutput( buffer.getData() ) );

                    buffer.reset();

                    return true;
                }

                tag = closing = false;
            }
        }
    }

    @Override
    public LongWritable getCurrentKey() throws IOException, InterruptedException {

        return key;
    }

    @Override
    public Text getCurrentValue() throws IOException, InterruptedException {

        return value;
    }

    @Override
    public float getProgress() throws IOException, InterruptedException {

        float total = ( float ) end - start;
        long read = input.getPos() - start;

        return read / total;
    }

    @Override
    public void close() throws IOException {

        input.close();
    }

    private String getCurrentTag() throws IOException {

        String output = "";

        while ( true ) {

            byte in = ( byte ) input.read();

            buffer.write( in );

            if ( in == ( byte ) ' ' || in == (byte) '>' ) return output;

            output += ( char ) in;
        }
    }

    private String minimizeOutput( byte[] formattedOutput ) {

        String formattedString = new String( formattedOutput, StandardCharsets.UTF_8 );

        Scanner scanner = new Scanner( formattedString );

        String minimizedOutput = "";

        while ( scanner.hasNextLine() ) minimizedOutput += scanner.nextLine().trim();

        return minimizedOutput;
    }
}
