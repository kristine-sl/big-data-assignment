package warmup.word_count;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import xml.XmlInputFormat;

public class WordCount extends Configured implements Tool {

    public static void main( String[] args ) {

        try {

            System.exit( new WordCount().run( args ) );

        } catch ( Exception e ) {

            e.printStackTrace();
            System.exit( -1 );
        }
    }

    public int run( String[] args ) throws Exception {

        Configuration config = new Configuration();

        config.addResource( "xml_config.xml" );

        final Job job = Job.getInstance( config );

        job.setJarByClass( WordCount.class );
        job.setJobName( "DBLP word counter" );
        job.setMapperClass( WordCountMapper.class );
        job.setReducerClass( WordCountReducer.class );
        job.setInputFormatClass( XmlInputFormat.class );

        job.setOutputKeyClass( Text.class );            // Word
        job.setOutputValueClass( IntWritable.class );   // Sum

        FileInputFormat.addInputPath( job, new Path( args[ 0 ] ) );
        FileOutputFormat.setOutputPath( job, new Path( args[ 1 ] ) );

        return job.waitForCompletion( true ) ? 0 : 1;
    }
}