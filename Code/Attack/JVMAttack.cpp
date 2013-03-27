#include "stdafx.h"

#using <System.dll>
using namespace System;
using namespace System::IO;
using namespace System::Diagnostics;
using namespace System::ComponentModel;
using namespace System::Threading;

#include <cstdio>

int _tmain(int argc, _TCHAR* argv[])
{
	array<Process^>^ procList = Process::GetProcessesByName("java");
	for each ( Process^ p in procList)
	{
		printf("Found Java executable with PID %d...\n", p->Id);
		Process^ debug = gcnew Process();
		try
        {
            printf("Starting debugger to capture memory contents...");
			debug->StartInfo->FileName = "jdb.exe";
			debug->StartInfo->Arguments = "-connect sun.jvm.hotspot.jdi.SAPIDAttachingConnector:pid=" + p->Id;
			debug->StartInfo->UseShellExecute = false;
			debug->StartInfo->CreateNoWindow = false;
			debug->StartInfo->RedirectStandardInput = true;
			debug->Start();
			printf("Done.\n");
			Thread::Sleep(200);
			printf("Searching for variable value \"verified\"...");
			StreamWriter^ input = debug->StandardInput;
			if(input)
			{
				input->WriteLine("threads");
				input->WriteLine("thread 0x4");
				input->WriteLine("print MasterDevice.verified");
			}
			Thread::Sleep(2000);
        }
        catch (Exception^ e)
        {
            Console::WriteLine( e->Message );
        }
	}
	return 0;
}